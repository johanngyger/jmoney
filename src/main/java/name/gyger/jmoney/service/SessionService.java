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

import name.gyger.jmoney.model.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Service
@Transactional
public class SessionService {

    @PersistenceContext
    private EntityManager em;

    public Session getSession() {
        Query q = em.createQuery("SELECT s FROM Session s LEFT JOIN FETCH s.rootCategory LEFT JOIN FETCH s.splitCategory LEFT JOIN FETCH s.transferCategory");
        return (Session) q.getSingleResult();
    }

    public boolean isSessionAvailable() {
        Query q = em.createQuery("SELECT COUNT(s) FROM Session s");
        return 1 == (Long) q.getSingleResult();
    }

}
