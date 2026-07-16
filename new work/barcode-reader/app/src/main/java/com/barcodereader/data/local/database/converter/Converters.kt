package com.barcodereader.data.local.database.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room Type Converters
 *
 * Converts between Kotlin types and database-storable types.
 */
class Converters {

    /**
     * Convert timestamp to Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Convert Date to timestamp
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}