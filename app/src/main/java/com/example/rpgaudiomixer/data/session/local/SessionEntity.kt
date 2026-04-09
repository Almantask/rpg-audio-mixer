package com.example.rpgaudiomixer.data.session.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = CampaignEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaignId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("campaignId")],
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
    val lastOpenedSceneId: Long? = null,
    val lastOpenedAtMillis: Long? = null,
)
