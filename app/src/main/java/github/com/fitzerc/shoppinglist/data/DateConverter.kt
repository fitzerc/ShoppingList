package github.com.fitzerc.shoppinglist.data

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromDateType(value: Date) = value.time

    @TypeConverter
    fun toUuidType(value: Long): Date = Date(value)
}