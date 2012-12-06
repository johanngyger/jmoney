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
import name.gyger.jmoney.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ReportController {

    @Inject
    private ReportService reportService;

    private Date parseDate(String dateString) {
        Date result = null;
        if (dateString != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                result = sdf.parse(dateString);
            } catch (ParseException e) {
                // ignore
            }
        }
        return result;
    }

    @RequestMapping(value = "/reports/balances", method = RequestMethod.GET)
    @ResponseBody
    public List<BalanceDto> getBalances(@RequestParam(value = "date", required = false) String dateString) {
        Date date = parseDate(dateString);

        return reportService.getBalances(date);
    }

    @RequestMapping(value = "/reports/cash-flows", method = RequestMethod.GET)
    @ResponseBody
    public List<CashFlowDto> getCashFlow(@RequestParam(value = "fromDate", required = false) String fromDateString,
                                        @RequestParam(value = "toDate", required = false) String toDateString) {
        Date fromDate = parseDate(fromDateString);
        Date toDate = parseDate(toDateString);

        return reportService.getCashFlow(fromDate, toDate);
    }


}
