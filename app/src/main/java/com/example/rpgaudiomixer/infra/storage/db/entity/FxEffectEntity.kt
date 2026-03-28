package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rpgaudiomixer.infra.storage.db.Converters

@Entity(tableName = "fx_effects")
@TypeConverters(Converters::class)
data class FxEffectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "track_file_path") val trackFilePath: String,
    @ColumnInfo(name = "tags") val tags: List<String> = emptyList(),
    @ColumnInfo(name = "duration_ms") val durationMs: Long = 0L,
    @ColumnInfo(name = "play_count") val playCount: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)
