package github.com.fitzerc.shoppinglist.data

import androidx.room.TypeConverter
import java.util.*

class UuidConverter {
    @TypeConverter
    fun fromUuidType(value: UUID): String {
        return value.toString()
    }

    @TypeConverter
    fun toUuidType(value: String): UUID = UUID.fromString(value)
}