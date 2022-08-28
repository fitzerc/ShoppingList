package github.com.fitzerc.shoppinglist.data.access.room

import androidx.room.*
import github.com.fitzerc.shoppinglist.data.EntryDto
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface EntryDao {
    @Query("SELECT * FROM entry where list_id =:listId")
    fun getEntries(listId: UUID): Flow<List<EntryDto>>

    @Query("SELECT * FROM entry where id =:id")
    suspend fun getEntryById(id: UUID): EntryDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EntryDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entry: EntryDto)

    @Query("DELETE FROM entry WHERE list_id =:listId")
    suspend fun deleteAll(listId: UUID)

    @Delete
    suspend fun delete(entry: EntryDto)
}