package com.example.rpgaudiomixer.data.session.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.domain.model.Session

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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
)

data class SessionListItemEntity(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
    val sceneCount: Int,
)

fun SessionEntity.asDomain(sceneCount: Int = 0): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    dateMillis = dateMillis,
    coverArtUri = coverArtUri,
    sceneCount = sceneCount,
)

fun SessionListItemEntity.asDomain(): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    dateMillis = dateMillis,
    coverArtUri = coverArtUri,
    sceneCount = sceneCount,
)
