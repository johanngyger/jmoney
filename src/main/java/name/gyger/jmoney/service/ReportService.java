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

package name.gyger.jmoney.service;

import name.gyger.jmoney.dto.BalanceDto;
import name.gyger.jmoney.dto.CashFlowDto;
import name.gyger.jmoney.model.Account;
import name.gyger.jmoney.model.Category;
import name.gyger.jmoney.model.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Service
@Transactional
public class ReportService {

    @Inject
    private SessionService sessionService;

    @Inject
    private CategoryService categoryService;

    @PersistenceContext
    private EntityManager em;

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

    private Map<Long, Long> getEntrySumsByAccountId(Date date) {
        Map<Long, Long> result = new HashMap<Long, Long>();

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
        for (Object resultItem : resultList) {
            Object[] resultItemArray = (Object[]) resultItem;
            Long accountId = (Long) resultItemArray[0];
            Long sum = (Long) resultItemArray[1];
            result.put(accountId, sum);
        }

        return result;
    }

    private Map<Long, Long> getEntrySumsByCategoryId(Date from, Date to) {
        Map<Long, Long> result = new HashMap<Long, Long>();

        String queryString = "SELECT e.category.id, SUM(e.amount) FROM Entry e " +
                " WHERE e.date > :from AND e.date <= :to" +
                " GROUP BY e.category.id";
        Query q = em.createQuery(queryString);
        q.setParameter("from", from);
        q.setParameter("to", to);

        List resultList = q.getResultList();
        for (Object resultItem : resultList) {
            Object[] resultItemArray = (Object[]) resultItem;
            Long accountId = (Long) resultItemArray[0];
            Long sum = (Long) resultItemArray[1];
            result.put(accountId, sum);
        }

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
            Long sum = (Long) entrySums.get(category.getId());
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
