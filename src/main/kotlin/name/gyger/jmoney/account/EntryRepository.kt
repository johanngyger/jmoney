package name.gyger.jmoney.account

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface EntryRepository : CrudRepository<Entry, Long> {

    @Query("SELECT count(e) FROM Entry e WHERE e.account.id = :accountId")
    fun count(@Param("accountId") accountId: Long): Long

    @Query("SELECT e FROM Entry e WHERE e.splitEntry.id = :entryId")
    fun findSubEntries(@Param("entryId") entryId: Long): List<Entry>

    @Query("SELECT e FROM Entry e LEFT JOIN FETCH e.category WHERE e.account.id = :accountId " +
            "ORDER BY CASE WHEN e.date IS NULL THEN 1 ELSE 0 END, e.date, e.creation")
    fun findEntriesForAccount(@Param("accountId") accountId: Long): List<Entry>

}
