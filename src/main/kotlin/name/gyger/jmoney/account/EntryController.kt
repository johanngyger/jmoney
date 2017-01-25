package name.gyger.jmoney.account

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/accounts")
class EntryController(private val entryService: EntryService,
                      private val entryRepository: EntryRepository) {

    @GetMapping("/{accountId}/entries/count")
    fun getEntryCount(@PathVariable accountId: Long): Long {
        return entryRepository.count(accountId)
    }

    @GetMapping("/{accountId}/entries")
    fun getEntries(@PathVariable accountId: Long,
                   @RequestParam(value = "page", required = false) page: Int?,
                   @RequestParam(value = "filter", required = false) filter: String?): List<Entry> {
        return entryService.getEntries(accountId, page, filter)
    }

    @GetMapping("/{accountId}/entries/{entryId}")
    fun getEntry(@PathVariable accountId: Long, @PathVariable entryId: Long): Entry {
        return entryService.getEntry(entryId)
    }

    @PostMapping("/{accountId}/entries")
    fun createEntry(@RequestBody entry: Entry, @PathVariable accountId: Long): Long {
        entry.accountId = accountId
        return entryService.deepSave(entry).id
    }

    @PutMapping("/{accountId}/entries/{entryId}")
    fun updateEntry(@PathVariable accountId: Long, @PathVariable entryId: Long, @RequestBody entry: Entry) {
        entry.id = entryId
        entry.accountId = accountId
        entryService.deepSave(entry)
    }

    @DeleteMapping("/{accountId}/entries/{entryId}")
    fun deleteEntry(@PathVariable accountId: Long, @PathVariable entryId: Long) {
        entryRepository.delete(entryId)
    }

}
