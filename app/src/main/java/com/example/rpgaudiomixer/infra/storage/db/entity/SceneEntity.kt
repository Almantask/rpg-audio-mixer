package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rpgaudiomixer.infra.storage.db.Converters

@Entity(tableName = "scenes")
@TypeConverters(Converters::class)
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "tags") val tags: List<String> = emptyList(),
    @ColumnInfo(name = "cover_art_uri") val coverArtUri: String? = null,
    @ColumnInfo(name = "soundboard_master_volume") val soundboardMasterVolume: Float = 0.8f,
    @ColumnInfo(name = "atmosphere_master_volume") val atmosphereMasterVolume: Float = 0.8f,
    @ColumnInfo(name = "play_count") val playCount: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)
