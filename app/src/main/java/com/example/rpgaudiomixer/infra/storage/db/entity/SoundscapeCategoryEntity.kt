package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "parent_category") val parentCategory: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)
