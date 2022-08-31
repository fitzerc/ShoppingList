package github.com.fitzerc.shoppinglist

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.com.fitzerc.shoppinglist.data.EntryDto
import github.com.fitzerc.shoppinglist.data.ListDto
import github.com.fitzerc.shoppinglist.data.access.room.ShoppingListDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val db: ShoppingListDatabase) :
    ViewModel() {
    private val _lists = MutableStateFlow<List<ListDto>>(emptyList())
    val lists = _lists.asStateFlow()

    var currentList: ListDto = if (lists.value.isEmpty())
        ListDto(UUID.randomUUID(), "Placeholder", "")
    else
        lists.value[0]
    set(value) {
        field = value
        viewModelScope.launch(Dispatchers.IO) {
            if (lists.value.isEmpty()) {
                Log.d("DataAccess", "No lists found")
            }

            db.entryDao().getEntries(currentList.id)
                .distinctUntilChanged()
                .collect {
                    if (it.isNullOrEmpty()) {
                        Log.d("DataAccess", "No entries found for list: ${currentList.name}")
                    }
                    _currentEntries.value = it
                }
        }
    }

    private val _currentEntries = MutableStateFlow<List<EntryDto>>(emptyList())
    val currentEntries = _currentEntries.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllLists()
                .collect {
                    if (it.isEmpty()) {
                        Log.d("DataAccess", "No lists found")
                    } else {
                        _lists.value = it
                        currentList = _lists.value[0]
                    }
                }
        }
    }

    private suspend fun getAllLists(): Flow<List<ListDto>> {
        return db.listDao().getLists().distinctUntilChanged()
    }

    suspend fun addList(list: ListDto) = viewModelScope.launch { db.listDao().insert(list) }
    suspend fun deleteList(list: ListDto) = viewModelScope.launch { db.listDao().delete(list) }
    suspend fun deleteAllLists() = viewModelScope.launch {
        db.listDao().deleteAll()
        getAllLists()
            .collect {
                if (it.isNullOrEmpty()) {
                    Log.d("DataAccess", "No Lists found")
                }
                _lists.value = it
            }
    }

    suspend fun updateList(list: ListDto) = viewModelScope.launch { db.listDao().update(list) }
    suspend fun getList(listId: UUID) = viewModelScope.launch { db.listDao().getListById(listId) }

    suspend fun addEntry(entry: EntryDto) = viewModelScope.launch { db.entryDao().insert(entry) }
    suspend fun deleteEntry(entry: EntryDto) = viewModelScope.launch { db.entryDao().delete(entry) }
    suspend fun deleteAllEntries(listId: UUID) = viewModelScope.launch { db.entryDao().deleteAll(listId) }
}