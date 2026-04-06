package com.example.rpgaudiomixer.data.session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.data.campaign.CampaignEntity
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
    val date: Long = 0L,
    val coverArtUri: String? = null,
)

fun SessionEntity.toDomain(): Session = Session(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date,
    coverArtUri = coverArtUri,
)

fun Session.toEntity(): SessionEntity = SessionEntity(
    id = id,
    campaignId = campaignId,
    name = name,
    date = date,
    coverArtUri = coverArtUri,
)
