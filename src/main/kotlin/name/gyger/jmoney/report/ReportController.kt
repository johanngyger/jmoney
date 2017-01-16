package name.gyger.jmoney.report

import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.util.parse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/rest/reports")
class ReportController(private val reportService: ReportService) {

    @GetMapping("/balances")
    fun getBalances(@RequestParam(value = "date", required = false) dateString: String?): List<Balance> {
        val date = parse(dateString)
        return reportService.getBalances(date)
    }

    @GetMapping("/cash-flows")
    fun getCashFlow(@RequestParam(value = "fromDate", required = false) fromDateString: String?,
                    @RequestParam(value = "toDate", required = false) toDateString: String?): List<CashFlow> {
        val fromDate = parse(fromDateString)
        val toDate = parse(toDateString)
        return reportService.getCashFlow(fromDate, toDate)
    }

    @GetMapping("/entries-with-category")
    fun getEntries(@RequestParam(value = "categoryId") categoryId: Long,
                   @RequestParam(value = "fromDate") fromDateString: String,
                   @RequestParam(value = "toDate") toDateString: String): List<Entry> {
        val from = parse(fromDateString)
        val to = parse(toDateString)
        return reportService.getEntriesForCategory(categoryId, from, to)
    }

    @GetMapping("/consitency/inconsistent-split-entries")
    fun getInconsistentSplitEntries(): List<Entry> {
        return reportService.inconsistentSplitEntries
    }

    @GetMapping("/consitency/entries-without-category")
    fun getEntriesWithoutCategory(): List<Entry> {
        return reportService.entriesWithoutCategory
    }

}
