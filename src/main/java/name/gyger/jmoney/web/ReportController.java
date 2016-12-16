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

package name.gyger.jmoney.web;

import name.gyger.jmoney.dto.BalanceDto;
import name.gyger.jmoney.dto.CashFlowDto;
import name.gyger.jmoney.dto.EntryDto;
import name.gyger.jmoney.service.EntryService;
import name.gyger.jmoney.service.ReportService;
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

    private final EntryService entryService;

    public ReportController(ReportService reportService, EntryService entryService) {
        this.reportService = reportService;
        this.entryService = entryService;
    }


    @RequestMapping(path = "/balances", method = RequestMethod.GET)
    public List<BalanceDto> getBalances(@RequestParam(value = "date", required = false) String dateString) {
        Date date = DateUtil.parse(dateString);
        return reportService.getBalances(date);
    }

    @RequestMapping(path = "/cash-flows", method = RequestMethod.GET)
    public List<CashFlowDto> getCashFlow(@RequestParam(value = "fromDate", required = false) String fromDateString,
                                        @RequestParam(value = "toDate", required = false) String toDateString) {
        Date fromDate = DateUtil.parse(fromDateString);
        Date toDate = DateUtil.parse(toDateString);

        return reportService.getCashFlow(fromDate, toDate);
    }

    @RequestMapping(path = "/entries-with-category", method = RequestMethod.GET)
    public List<EntryDto> getEntries(@RequestParam(value = "categoryId") long categoryId,
                                     @RequestParam(value = "fromDate") String fromDateString,
                                     @RequestParam(value = "toDate") String toDateString) {
        Date from = DateUtil.parse(fromDateString);
        Date to = DateUtil.parse(toDateString);
        return entryService.getEntriesForCategory(categoryId, from, to);
    }

    @RequestMapping(path = "/consitency/inconsistent-split-entries", method = RequestMethod.GET)
    public List<EntryDto> getInconsistentSplitEntries() {
        return entryService.getInconsistentSplitEntries();
    }

    @RequestMapping(path = "/consitency/entries-without-category", method = RequestMethod.GET)
    public List<EntryDto> getEntriesWithoutCategory() {
        return entryService.getEntriesWithoutCategory();
    }

}
