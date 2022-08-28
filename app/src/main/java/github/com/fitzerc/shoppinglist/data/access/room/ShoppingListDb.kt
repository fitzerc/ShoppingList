package github.com.fitzerc.shoppinglist.data.access.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.com.fitzerc.shoppinglist.data.DateConverter
import github.com.fitzerc.shoppinglist.data.EntryDto
import github.com.fitzerc.shoppinglist.data.ListDto
import github.com.fitzerc.shoppinglist.data.UuidConverter

@Database(entities = [ListDto::class, EntryDto::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, UuidConverter::class)
abstract class ShoppingListDatabase : RoomDatabase() {
    abstract fun listDao(): ListDao
    abstract fun entryDao(): EntryDao
}