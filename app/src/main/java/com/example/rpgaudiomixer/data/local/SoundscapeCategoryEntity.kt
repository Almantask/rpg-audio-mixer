package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null
)
