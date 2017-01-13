package name.gyger.jmoney.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/accounts")
public class EntryController {

    private final Logger log = LoggerFactory.getLogger(EntryController.class);

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @RequestMapping(path = "/{accountId}/entries/count", method = RequestMethod.GET)
    public long getEntryCount(@PathVariable long accountId) {
        return entryService.getEntryCount(accountId);
    }

    @RequestMapping(path = "/{accountId}/entries", method = RequestMethod.GET)
    public List<Entry> getEntries(@PathVariable long accountId,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "filter", required = false) String filter) {
        return entryService.getEntries(accountId, page, filter);
    }

    @RequestMapping(path = "/{accountId}/entries/{entryId}", method = RequestMethod.GET)
    public Entry getEntry(@PathVariable long accountId, @PathVariable long entryId) {
        return entryService.getEntry(entryId);
    }

    @RequestMapping(path = "/{accountId}/entries", method = RequestMethod.POST)
    public long createEntry(@RequestBody Entry entry, @PathVariable long accountId) {
        entry.setAccountId(accountId);
        return entryService.createEntry(entry);
    }

    @RequestMapping(path = "/{accountId}/entries/{entryId}", method = RequestMethod.PUT)
    public void updateEntry(@PathVariable long accountId, @PathVariable long entryId, @RequestBody Entry entry) {
        entry.setId(entryId);
        entry.setAccountId(accountId);
        entryService.updateEntry(entry);
    }

    @RequestMapping(path = "/{accountId}/entries/{entryId}", method = RequestMethod.DELETE)
    public void deleteEntry(@PathVariable long accountId, @PathVariable long entryId) {
        entryService.deleteEntry(entryId);
    }

}
