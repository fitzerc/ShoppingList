package github.com.fitzerc.shoppinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.com.fitzerc.shoppinglist.data.ListDto
import github.com.fitzerc.shoppinglist.data.access.room.ShoppingListDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val db: ShoppingListDatabase) : ViewModel() {
    private val _lists = MutableStateFlow<List<ListDto>>(emptyList())
    val lists = _lists.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllLists()
                .collect {
                    if (it.isNullOrEmpty()) {
                        Log.d("DataAccess", "No lists found")
                    }
                    _lists.value = it
                }
        }
    }

    private suspend fun getAllLists() : Flow<List<ListDto>> {
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
}