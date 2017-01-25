package name.gyger.jmoney.report

import name.gyger.jmoney.account.Entry
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.*

interface ReportRepository : Repository<Entry, Long> {

    @Query("SELECT NEW name.gyger.jmoney.report.ReportItem(e.account.id, SUM(e.amount)) " +
            "FROM Entry e " +
            "WHERE e.account.id IS NOT NULL AND e.date <= :date " +
            "GROUP BY e.account.id")
    fun getEntrySumsByAccountId(@Param("date") date: Date?): List<ReportItem>


    @Query("SELECT NEW name.gyger.jmoney.report.ReportItem(e.category.id, SUM(e.amount)) " +
            "FROM Entry e " +
            " WHERE e.date >= :from AND e.date <= :to AND e.category.id IS NOT NULL" +
            " GROUP BY e.category.id")
    fun getEntrySumsByCategoryId(@Param("from") from: Date?, @Param("to") to: Date?): List<ReportItem>


    @Query("SELECT NEW name.gyger.jmoney.report.ReportItem(e.splitEntry.id, SUM(e.amount)) " +
            "FROM Entry e " +
            "WHERE e.splitEntry.id IS NOT NULL " +
            "GROUP BY e.splitEntry.id")
    fun getSplitEntrySums(): List<ReportItem>

}