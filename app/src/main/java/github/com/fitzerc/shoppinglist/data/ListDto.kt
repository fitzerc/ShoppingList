package github.com.fitzerc.shoppinglist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull
import java.util.*

@Entity(tableName = "list")
data class ListDto(
    @PrimaryKey
    @NotNull
    @field:TypeConverters(UuidConverter::class)
    val id: UUID = UUID.randomUUID(),

    @NotNull
    val name: String,

    @NotNull
    val description: String)

