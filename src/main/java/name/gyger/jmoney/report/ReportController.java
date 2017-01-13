package name.gyger.jmoney.report;

import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.account.EntryService;
import name.gyger.jmoney.util.DateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService, EntryService entryService) {
        this.reportService = reportService;
    }


    @RequestMapping(path = "/balances", method = RequestMethod.GET)
    public List<Balance> getBalances(@RequestParam(value = "date", required = false) String dateString) {
        Date date = DateUtil.parse(dateString);
        return reportService.getBalances(date);
    }

    @RequestMapping(path = "/cash-flows", method = RequestMethod.GET)
    public List<CashFlow> getCashFlow(@RequestParam(value = "fromDate", required = false) String fromDateString,
                                      @RequestParam(value = "toDate", required = false) String toDateString) {
        Date fromDate = DateUtil.parse(fromDateString);
        Date toDate = DateUtil.parse(toDateString);

        return reportService.getCashFlow(fromDate, toDate);
    }

    @RequestMapping(path = "/entries-with-category", method = RequestMethod.GET)
    public List<Entry> getEntries(@RequestParam(value = "categoryId") long categoryId,
                                  @RequestParam(value = "fromDate") String fromDateString,
                                  @RequestParam(value = "toDate") String toDateString) {
        Date from = DateUtil.parse(fromDateString);
        Date to = DateUtil.parse(toDateString);
        return reportService.getEntriesForCategory(categoryId, from, to);
    }

    @RequestMapping(path = "/consitency/inconsistent-split-entries", method = RequestMethod.GET)
    public List<Entry> getInconsistentSplitEntries() {
        return reportService.getInconsistentSplitEntries();
    }

    @RequestMapping(path = "/consitency/entries-without-category", method = RequestMethod.GET)
    public List<Entry> getEntriesWithoutCategory() {
        return reportService.getEntriesWithoutCategory();
    }

}
