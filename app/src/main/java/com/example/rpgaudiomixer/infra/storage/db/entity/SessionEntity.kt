package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = CampaignEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaign_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("campaign_id")],
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "campaign_id") val campaignId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "cover_art_uri") val coverArtUri: String? = null,
    @ColumnInfo(name = "played_at") val playedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)
