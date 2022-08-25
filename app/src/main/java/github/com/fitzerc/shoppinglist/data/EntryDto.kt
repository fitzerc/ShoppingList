package github.com.fitzerc.shoppinglist.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "entry")
data class EntryDto
constructor(
    @PrimaryKey
    @NotNull
    @field:TypeConverters(UuidConverter::class)
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "list_id")
    @NotNull
    @field:TypeConverters(UuidConverter::class)
    val listId: UUID,

    @NotNull
    val name: String,

    val description: String,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int,

    @field:TypeConverters(DateConverter::class)
    @ColumnInfo(name = "entry_date")
    val entryDate: Date = Date())

