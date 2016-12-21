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

package name.gyger.jmoney.account;

import name.gyger.jmoney.session.Session;
import name.gyger.jmoney.session.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class AccountService {

    @PersistenceContext
    private EntityManager em;

    private final SessionService sessionService;

    public AccountService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public List<Account> getAccounts() {
        List<Account> accounts = sessionService.getSession().getAccounts();
        accounts.forEach(a -> {
            em.detach(a);
            a.setSession(null);
        });
        return accounts;
    }

    public Account getAccount(long accountId) {
        return em.find(Account.class, accountId);
    }

    public long createAccount(Account a) {
        Session s = sessionService.getSession();
        a.setSession(s);
        a.setParent(s.getTransferCategory());
        em.persist(a);
        return a.getId();
    }

    public void updateAccount(Account account) {
        em.merge(account);
    }

    public void deleteAccount(long accountId) {
        Account a = em.find(Account.class, accountId);
        em.remove(a);
    }

}
