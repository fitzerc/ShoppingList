package github.com.fitzerc.shoppinglist.data.access

import github.com.fitzerc.shoppinglist.data.ListDto
import java.util.*

interface LocalListDbAccess {
    fun createList(list: ListDto): UUID
    fun replaceList(list: ListDto): UUID
    fun getLists(): List<ListDto>
    fun getList(listId: UUID): ListDto
    fun getList(listName: String): ListDto
    fun deleteList(listId: UUID)
    fun deleteAllLists()
}