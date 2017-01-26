package name.gyger.jmoney.report

import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.account.EntryService
import name.gyger.jmoney.util.parse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/reports")
class ReportController(private val reportService: ReportService,
                       private val entryService: EntryService) {

    @GetMapping("/balances")
    fun getBalances(@RequestParam(value = "date", required = false) dateString: String?): List<Balance> {
        val date = parse(dateString)
        return reportService.getBalances(date)
    }

    @GetMapping("/cash-flows")
    fun getCashFlow(@RequestParam(value = "fromDate") fromDateString: String,
                    @RequestParam(value = "toDate") toDateString: String): List<CashFlow> {
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
        return entryService.getEntriesForCategory(categoryId, from, to)
    }

    @GetMapping("/consistency/inconsistent-split-entries")
    fun getInconsistentSplitEntries(): List<Entry> {
        return reportService.getInconsistentSplitEntries()
    }

    @GetMapping("/consistency/entries-without-category")
    fun getEntriesWithoutCategory(): List<Entry> {
        return entryService.getEntriesWithoutCategory()
    }

}
