package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Session entity - represents a game session within a campaign.
 *
 * A session belongs to a campaign and can have multiple scenes linked to it.
 */
@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = CampaignEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaign_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("campaign_id")]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "campaign_id")
    val campaignId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "cover_art_uri")
    val coverArtUri: String? = null
)

/**
 * DAO for Session entities.
 */
@Dao
interface SessionDao {

    /**
     * Observe all sessions for a specific campaign.
     * Ordered by date descending (most recent first).
     */
    @Query("SELECT * FROM sessions WHERE campaign_id = :campaignId ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    /**
     * Insert or update a session.
     */
    @Upsert
    suspend fun upsert(session: SessionEntity): Long

    /**
     * Delete a session by ID.
     */
    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun delete(id: Long)

    /**
     * Get a session by ID (one-shot).
     */
    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): SessionEntity?
}
