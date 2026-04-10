package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val isDemoContent: Boolean,
)
