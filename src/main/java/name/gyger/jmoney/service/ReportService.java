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
import name.gyger.jmoney.model.CategoryType;
import name.gyger.jmoney.model.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReportService {

    @Inject
    private SessionService sessionService;

    @PersistenceContext
    private EntityManager em;

    public List<BalanceDto> getBalances(Date date) {
        Session session = sessionService.getSession();
        List<BalanceDto> result = new ArrayList<>();

        long totalBalance = 0;
        for (Account account : session.getAccounts()) {
            String queryString = "SELECT SUM(e.amount) FROM Entry e WHERE e.account.id = :accountId";
            if (date != null) {
                queryString += " AND e.date <= :date";
            }
            Query q = em.createQuery(queryString);
            q.setParameter("accountId", account.getId());
            if (date != null) {
                q.setParameter("date", date);
            }
            Long sum = (Long) q.getSingleResult();

            long balance = 0;
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
        List<CashFlowDto> resultList = new ArrayList<>();
        Category root = session.getRootCategory();

        long totalIncome = 0;
        long totalExpense = 0;

        for (Category child : root.getChildren()) {
            List<CashFlowDto> subList = new ArrayList<>();
            calculateCashFlowForCategory(subList, child, null, from, to);

            long income = 0;
            long expense = 0;
            for (CashFlowDto cashFlow : subList) {
                income += toZeroIfNull(cashFlow.getIncome());
                expense += toZeroIfNull(cashFlow.getExpense());
            }

            if (income != 0 || expense != 0) {
                CashFlowDto childDto = new CashFlowDto(child.getName() + " (Gesamt)", income, expense, income - expense);
                childDto.setTotal(true);
                subList.add(childDto);
            }

            totalIncome += income;
            totalExpense += expense;
            resultList.addAll(subList);
        }

        CashFlowDto total = new CashFlowDto("Gesamttotal", totalIncome, totalExpense, totalIncome - totalExpense);
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

    private void calculateCashFlowForCategory(List<CashFlowDto> resultList, Category category, String parentName, Date from, Date to) {
        String name = createCategoryName(category, parentName);

        if (category.getType() == CategoryType.NORMAL) {
            Query q = createCategorySumQuery(category, from, to);
            Long sum = (Long) q.getSingleResult();
            createCategoryFlowDto(resultList, name, sum);
        }

        for (Category child : category.getChildren()) {
            calculateCashFlowForCategory(resultList, child, name, from, to);
        }
    }

    private Query createCategorySumQuery(Category category, Date from, Date to) {
        String queryString = "SELECT SUM(e.amount) FROM Entry e WHERE e.category.id = :categoryId " +
                "AND e.date > :from AND e.date <= :to";
        Query q = em.createQuery(queryString);
        q.setParameter("categoryId", category.getId());
        q.setParameter("from", from);
        q.setParameter("to", to);
        return q;
    }

    private void createCategoryFlowDto(List<CashFlowDto> resultList, String name, Long sum) {
        if (sum != null) {
            Long income = null;
            Long expense = null;

            if (sum > 0) {
                income = sum;
            } else if (sum < 0) {
                expense = -sum;
            }

            CashFlowDto dto = new CashFlowDto(name, income, expense, null);
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
