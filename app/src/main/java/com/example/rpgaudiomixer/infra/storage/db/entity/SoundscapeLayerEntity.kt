package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "soundscape_layers",
    foreignKeys = [
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("category_id")],
)
data class SoundscapeLayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "track_file_path") val trackFilePath: String,
    @ColumnInfo(name = "intensity") val intensity: Int = 1,
    @ColumnInfo(name = "mix") val mix: Float = 0.8f,
    @ColumnInfo(name = "duration_ms") val durationMs: Long = 0L,
)
