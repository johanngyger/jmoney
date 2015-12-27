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

package name.gyger.jmoney.web.controller;

import name.gyger.jmoney.dto.EntryDetailsDto;
import name.gyger.jmoney.dto.EntryDto;
import name.gyger.jmoney.service.EntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rest/accounts")
public class EntryController {

    private final Logger log = LoggerFactory.getLogger(EntryController.class);

    @Autowired
    private EntryService entryService;

    @RequestMapping(path = "/{accountId}/entries/count", method = RequestMethod.GET)
    @ResponseBody
    public long getEntryCount(@PathVariable long accountId) {
        return entryService.getEntryCount(accountId);
    }

    @RequestMapping(path = "/{accountId}/entries", method = RequestMethod.GET)
    @ResponseBody
    public List<EntryDto> getEntries(@PathVariable long accountId,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "filter", required = false) String filter) {
        return entryService.getEntries(accountId, page, filter);
    }

    @RequestMapping(path = "/{accountId}/entries/{entryId}", method = RequestMethod.GET)
    @ResponseBody
    public EntryDetailsDto getEntry(@PathVariable long accountId, @PathVariable long entryId) {
        return entryService.getEntry(entryId);
    }

    @RequestMapping(path = "/{accountId}/entries", method = RequestMethod.POST)
    @ResponseBody
    public long createEntry(@RequestBody EntryDetailsDto entry, @PathVariable long accountId) {
        entry.setAccountId(accountId);
        return entryService.createEntry(entry);
    }

    @RequestMapping(path = "/{accountId}/entries/{entryId}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateEntry(@PathVariable long accountId, @PathVariable long entryId, @RequestBody EntryDetailsDto entry) {
        entryService.updateEntry(entry);
    }

    @RequestMapping(path = "/{accountId}/entries/{entryId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteEntry(@PathVariable long accountId, @PathVariable long entryId) {
        entryService.deleteEntry(entryId);
    }

}
