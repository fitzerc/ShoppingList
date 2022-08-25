package github.com.fitzerc.shoppinglist.data.access

import github.com.fitzerc.shoppinglist.data.EntryDto
import java.util.*

interface LocalEntryDbAccess {
    fun createEntry(entry: EntryDto): UUID
    fun createEntries(entries: List<EntryDto>, replace: Boolean = true)
    fun updateSortOrder(entries: List<EntryDto>)
    fun getListEntries(listId: UUID): List<EntryDto>
    fun getListEntries(listName: String): List<EntryDto>
    fun getEntry(entryId: UUID): EntryDto
    fun deleteEntry(entryId: UUID)
    fun deleteListEntries(listId: UUID)
}