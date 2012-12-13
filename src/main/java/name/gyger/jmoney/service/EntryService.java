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

import name.gyger.jmoney.dto.EntryDto;
import name.gyger.jmoney.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class EntryService {

    @PersistenceContext
    private EntityManager em;

    public long getEntryCount(long accountId) {
        Query q = em.createQuery("SELECT count(e) FROM Entry e WHERE e.account.id = :id");
        q.setParameter("id", accountId);
        return (Long) q.getSingleResult();
    }

    public List<EntryDto> getEntries(long accountId, Integer page, String filter) {
        Query q = em.createQuery("SELECT e FROM Entry e LEFT JOIN FETCH e.category WHERE e.account.id = :id" +
                " ORDER BY CASE WHEN e.date IS NULL THEN 1 ELSE 0 END, e.date, e.creation");
        q.setParameter("id", accountId);

        @SuppressWarnings("unchecked")
        List<Entry> entries = q.getResultList();
        List<EntryDto> result = new ArrayList<EntryDto>();
        EntryDto previousEntryDto = null;
        for (Entry entry : entries) {
            if (entry.contains(filter)) {
                EntryDto entryDto = new EntryDto(entry);
                result.add(entryDto);
                if (previousEntryDto == null) {
                    Account a = entry.getAccount();
                    entryDto.setBalance(entry.getAmount() + a.getStartBalance());
                } else {
                    entryDto.setBalance(entry.getAmount() + previousEntryDto.getBalance());
                }
                previousEntryDto = entryDto;
            }
        }

        Collections.reverse(result);

        if (page == null) {
            page = 1;
        }
        int count = result.size();
        int from = Math.min((page - 1) * 10, count);
        int to = Math.min((page - 1) * 10 + 10, count);
        result = result.subList(from, to);

        return result;
    }

    public EntryDto getEntry(long id) {
        Entry e = em.find(Entry.class, id);
        return new EntryDto(e);
    }

    public long createEntry(EntryDto dto) {
        Account a = em.find(Account.class, dto.getAccountId());
        Category c = em.find(Category.class, dto.getCategoryId());
        Entry e = new Entry();

        if (c instanceof Account) {
            Account otherAccount = (Account) c;

            Entry other = new Entry();
            other.setOther(e);
            other.setCategory(a);
            other.setAccount(otherAccount);

            e.setOther(other);

            em.persist(other);
        }

        dto.mapToModel(e);
        e.setAccount(a);
        e.setCategory(c);

        em.persist(e);

        return e.getId();
    }

    public void updateEntry(EntryDto dto) {
        Account a = em.find(Account.class, dto.getAccountId());
        Entry e = em.find(Entry.class, dto.getId());
        Category c = em.find(Category.class, dto.getCategoryId());

        if (c == null || c.getType() == CategoryType.NORMAL) {
            dto.mapToModel(e);
            e.setOther(null);
            //e.setSubEntries(null);
            e.setCategory(c);
        } else if (c instanceof Account) {
            Account otherAccount = (Account) c;

            Entry other = e.getOther();
            if (other == null) {
                other = new Entry();
                em.persist(other);
            }

            other.setOther(e);
            other.setCategory(a);
            other.setAccount(otherAccount);

            dto.mapToModel(e);
            e.setOther(other);
            //e.setSubEntries(null);
            e.setCategory(c);
        } else if (c.getType() == CategoryType.SPLIT) {
            // TODO
        }
    }

    public void deleteEntry(long entryId) {
        Entry e = em.find(Entry.class, entryId);
        em.remove(e);
    }
}
