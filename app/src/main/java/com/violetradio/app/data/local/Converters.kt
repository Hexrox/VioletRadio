package com.violetradio.app.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Long? {
        return value
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Long? {
        return value
    }
}
