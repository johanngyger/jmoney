package name.gyger.jmoney.account

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface EntryRepository : CrudRepository<Entry, Long> {

    @Query("SELECT count(e) FROM Entry e WHERE e.account.id = :accountId")
    fun count(@Param("accountId") accountId: Long): Long

    @Query("SELECT e FROM Entry e WHERE e.splitEntry.id = :entryId")
    fun findSubEntries(@Param("entryId") entryId: Long): List<Entry>

    @Query("SELECT e FROM Entry e LEFT JOIN FETCH e.category WHERE e.account.id = :accountId " +
            "ORDER BY CASE WHEN e.date IS NULL THEN 1 ELSE 0 END, e.date, e.creation")
    fun findEntriesForAccount(@Param("accountId") accountId: Long): List<Entry>

    @Modifying
    @Query("UPDATE Entry SET CATEGORY_ID = NULL WHERE CATEGORY_ID = :categoryId")
    fun deleteCategoryFromEntry(@Param("categoryId") categoryId: Long)

    @Query("SELECT e FROM Entry e WHERE e.category.id = null AND e.splitEntry = null ORDER BY " +
            "CASE WHEN e.date IS NULL THEN 0 ELSE 1 END, e.date DESC, e.creation DESC")
    fun getEntriesWithoutCategory(): List<Entry>

    @Query("SELECT e FROM Entry e WHERE e.category.id = :categoryId " +
            "AND e.date >= :from AND e.date <= :to ORDER BY e.date DESC, e.creation DESC")
    fun getEntriesForCategory(@Param("categoryId") categoryId: Long, @Param("from") fromDate: Date?,
                              @Param("to") to: Date?): List<Entry>

    @Query("SELECT e FROM Entry e WHERE e.category.id = :categoryId " +
            "ORDER BY CASE WHEN e.date IS NULL THEN 0 ELSE 1 END, e.date DESC, e.creation DESC")
    fun getInconsistentSplitEntries(@Param("categoryId") categoryId: Long): List<Entry>

}