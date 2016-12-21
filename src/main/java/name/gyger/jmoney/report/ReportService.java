/*
 * Copyright 2012 Johann Gyger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.gyger.jmoney.report;

import name.gyger.jmoney.account.Account;
import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.category.Category;
import name.gyger.jmoney.category.CategoryService;
import name.gyger.jmoney.session.Session;
import name.gyger.jmoney.session.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReportService {

    private final SessionService sessionService;

    private final CategoryService categoryService;

    @PersistenceContext
    private EntityManager em;

    public ReportService(SessionService sessionService, CategoryService categoryService) {
        this.sessionService = sessionService;
        this.categoryService = categoryService;
    }

    public List<BalanceDto> getBalances(Date date) {
        Session session = sessionService.getSession();
        List<BalanceDto> result = new ArrayList<BalanceDto>();

        Map<Long, Long> entrySums = getEntrySumsByAccountId(date);

        long totalBalance = 0;
        for (Account account : session.getAccounts()) {
            long balance = 0;
            Long sum = entrySums.get(account.getId());
            if (sum != null) {
                balance = sum;
            }
            balance += account.getStartBalance();
            totalBalance += balance;

            BalanceDto dto = new BalanceDto(account.getName(), balance);
            result.add(dto);
        }

        BalanceDto totalBalanceDto = new BalanceDto("Gesamt", totalBalance);
        totalBalanceDto.setTotal(true);
        result.add(totalBalanceDto);

        return result;
    }

    public List<CashFlowDto> getCashFlow(Date from, Date to) {
        Session session = sessionService.getSession();
        List<CashFlowDto> resultList = new ArrayList<CashFlowDto>();

        categoryService.prefetchCategories();
        Category root = session.getRootCategory();

        Map<Long, Long> entrySums = getEntrySumsByCategoryId(from, to);

        long totalIncome = 0;
        long totalExpense = 0;

        for (Category child : root.getChildren()) {
            List<CashFlowDto> subList = new ArrayList<CashFlowDto>();
            calculateCashFlowForCategory(subList, entrySums, child, null, from, to);

            long income = 0;
            long expense = 0;
            for (CashFlowDto cashFlow : subList) {
                income += toZeroIfNull(cashFlow.getIncome());
                expense += toZeroIfNull(cashFlow.getExpense());
            }

            if (income != 0 || expense != 0) {
                CashFlowDto childDto = new CashFlowDto(null, child.getName() + " (Gesamt)", income, expense, income - expense);
                childDto.setTotal(true);
                subList.add(childDto);
            }

            totalIncome += income;
            totalExpense += expense;
            resultList.addAll(subList);
        }

        CashFlowDto total = new CashFlowDto(null, "Gesamttotal", totalIncome, totalExpense, totalIncome - totalExpense);
        total.setTotal(true);
        resultList.add(total);
        return resultList;
    }

    public List<Entry> getInconsistentSplitEntries() {
        Category splitCategory = sessionService.getSession().getSplitCategory();

        TypedQuery<Entry> q = em.createQuery("SELECT e FROM Entry e WHERE e.category.id = :categoryId" +
                " ORDER BY CASE WHEN e.date IS NULL THEN 0 ELSE 1 END, e.date DESC, e.creation DESC", Entry.class);
        q.setParameter("categoryId", splitCategory.getId());

        Map<Long, Long> splitEntrySums = getSplitEntrySums();
        List<Entry> entries = q.getResultList();
        List<Entry> result = new ArrayList<Entry>();
        for (Entry entry : entries) {
            Long sum = splitEntrySums.get(entry.getId());
            if (sum == null) {
                sum = Long.valueOf(0);
            }
            if (entry.getAmount() != sum) {
                result.add(entry);
            }
        }

        return result;
    }

    public List<Entry> getEntriesWithoutCategory() {
        TypedQuery<Entry> q = em.createQuery("SELECT e FROM Entry e WHERE e.category.id = null AND e.splitEntry = null" +
                " ORDER BY CASE WHEN e.date IS NULL THEN 0 ELSE 1 END, e.date DESC, e.creation DESC", Entry.class);
        List<Entry> entries = q.getResultList();
        return entries;
    }

    public List<Entry> getEntriesForCategory(long categoryId, Date from, Date to) {
        TypedQuery<Entry> q = em.createQuery("SELECT e FROM Entry e WHERE e.category.id = :categoryId AND e.date >= :from AND e.date <= :to" +
                " ORDER BY e.date DESC, e.creation DESC", Entry.class);
        q.setParameter("categoryId", categoryId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        List<Entry> entries = q.getResultList();
        return entries;
    }

    private Map<Long, Long> getEntrySumsByAccountId(Date date) {

        String queryString = "SELECT e.account.id, SUM(e.amount) FROM Entry e";
        if (date != null) {
            queryString += " WHERE e.date <= :date";
        }
        queryString += " GROUP BY e.account.id";

        Query q = em.createQuery(queryString);
        if (date != null) {
            q.setParameter("date", date);
        }

        List resultList = q.getResultList();
        Map<Long, Long> result = ReportUtil.mapResult(resultList);
        return result;
    }

    private Map<Long, Long> getEntrySumsByCategoryId(Date from, Date to) {
        String queryString = "SELECT e.category.id, SUM(e.amount) FROM Entry e " +
                " WHERE e.date >= :from AND e.date <= :to" +
                " GROUP BY e.category.id";
        Query q = em.createQuery(queryString);
        q.setParameter("from", from);
        q.setParameter("to", to);

        List resultList = q.getResultList();
        Map<Long, Long> result = ReportUtil.mapResult(resultList);
        return result;
    }

    private Map<Long, Long> getSplitEntrySums() {
        String queryString = "SELECT e.splitEntry.id, SUM(e.amount) FROM Entry e GROUP BY e.splitEntry.id";
        Query q = em.createQuery(queryString);
        List resultList = q.getResultList();
        Map<Long, Long> result = ReportUtil.mapResult(resultList);
        return result;
    }

    private long toZeroIfNull(Long value) {
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    private void calculateCashFlowForCategory(List<CashFlowDto> resultList, Map<Long, Long> entrySums,
                                              Category category, String parentName, Date from, Date to) {
        String name = createCategoryName(category, parentName);

        if (category.getType() == Category.Type.NORMAL) {
            Long sum = entrySums.get(category.getId());
            createCategoryFlowDto(resultList, category.getId(), name, sum);
        }

        for (Category child : category.getChildren()) {
            calculateCashFlowForCategory(resultList, entrySums, child, name, from, to);
        }
    }

    private void createCategoryFlowDto(List<CashFlowDto> resultList, Long id, String name, Long sum) {
        if (sum != null) {
            Long income = null;
            Long expense = null;

            if (sum > 0) {
                income = sum;
            } else if (sum < 0) {
                expense = -sum;
            }

            CashFlowDto dto = new CashFlowDto(id, name, income, expense, null);
            resultList.add(dto);
        }
    }

    private String createCategoryName(Category category, String parentName) {
        String name = category.getName();
        if (parentName != null) {
            name = parentName + ":" + name;
        }
        return name;
    }

}
