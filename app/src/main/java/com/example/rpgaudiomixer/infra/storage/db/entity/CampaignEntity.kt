package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "cover_art_uri") val coverArtUri: String? = null,
    @ColumnInfo(name = "last_played_at") val lastPlayedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)
