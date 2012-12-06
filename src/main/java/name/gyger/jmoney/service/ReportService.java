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
import name.gyger.jmoney.model.SpecialCategory;
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
        long totalSum = 0;
        for (Category child : root.getChildren()) {
            long childSum = getCashFlowForCategory(resultList, child, null, from, to);
            if (childSum != 0) {
                CashFlowDto childDto = new CashFlowDto(child.getName() + " (Gesamt)", childSum);
                childDto.setTotal(true);
                resultList.add(childDto);
            }
            totalSum += childSum;
        }
        CashFlowDto total = new CashFlowDto("Gesamttotal", totalSum);
        total.setTotal(true);
        resultList.add(total);
        return resultList;
    }

    private long getCashFlowForCategory(List<CashFlowDto> resultList, Category category, String parentName, Date from, Date to) {
        long sum = 0;

        String name = category.getName();
        if (parentName != null) {
            name = parentName + ":" + name;
        }

        if (!(category instanceof SpecialCategory) && !(category instanceof Account)) {
            String queryString = "SELECT SUM(e.amount) FROM Entry e WHERE e.category.id = :categoryId " +
                    "AND e.date > :from AND e.date <= :to";
            Query q = em.createQuery(queryString);
            q.setParameter("categoryId", category.getId());
            q.setParameter("from", from);
            q.setParameter("to", to);
            Long qr = (Long) q.getSingleResult();

            if (qr != null) {
                sum = qr;
            }

            if (sum != 0) {
                CashFlowDto dto = new CashFlowDto(name, sum);
                resultList.add(dto);
            }
        }

        for (Category child : category.getChildren()) {
            sum += getCashFlowForCategory(resultList, child, name, from, to);
        }

        return sum;
    }
}
