package github.com.fitzerc.shoppinglist.data.access.room.ListDao

import androidx.room.*
import github.com.fitzerc.shoppinglist.data.ListDto
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ListDao {
    @Query("SELECT * FROM list")
    fun getLists(): Flow<List<ListDto>>

    @Query("SELECT * FROM list where id =:id")
    suspend fun getListById(id: UUID): ListDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: ListDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(list: ListDto)

    @Query("DELETE FROM list")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(list: ListDto)
}
