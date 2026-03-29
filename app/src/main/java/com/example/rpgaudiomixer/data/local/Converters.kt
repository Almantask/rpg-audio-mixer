package com.example.rpgaudiomixer.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>?): String = list?.joinToString(",") ?: ""

    @TypeConverter
    fun toStringList(data: String?): List<String> = data?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}
