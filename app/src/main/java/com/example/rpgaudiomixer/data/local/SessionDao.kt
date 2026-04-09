package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query(
        """
        SELECT sessions.id,
               sessions.campaignId,
               sessions.name,
               sessions.date,
               sessions.coverArtUri,
               sessions.lastOpenedSceneId,
               sessions.lastOpenedAt,
               COUNT(session_scene_cross_ref.sceneId) AS sceneCount
        FROM sessions
        LEFT JOIN session_scene_cross_ref
            ON sessions.id = session_scene_cross_ref.sessionId
        WHERE sessions.campaignId = :campaignId
          AND sessions.deletedAt IS NULL
        GROUP BY sessions.id
        ORDER BY sessions.date DESC, sessions.id DESC
        """
    )
    fun observeByCampaign(campaignId: Long): Flow<List<SessionSummaryEntity>>

    @Query(
        """
        SELECT sessions.id,
               sessions.campaignId,
               sessions.name,
               sessions.date,
               sessions.coverArtUri,
               sessions.lastOpenedSceneId,
               sessions.lastOpenedAt,
               COUNT(session_scene_cross_ref.sceneId) AS sceneCount
        FROM sessions
        LEFT JOIN session_scene_cross_ref
            ON sessions.id = session_scene_cross_ref.sessionId
        WHERE sessions.id = :sessionId
          AND sessions.deletedAt IS NULL
        GROUP BY sessions.id
        LIMIT 1
        """
    )
    fun observeById(sessionId: Long): Flow<SessionSummaryEntity?>

    @Query("SELECT * FROM sessions WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity): Long

    @Query(
        """
        UPDATE sessions
        SET lastOpenedSceneId = :sceneId,
            lastOpenedAt = :openedAt
        WHERE id = :sessionId
        """
    )
    suspend fun recordOpenedScene(sessionId: Long, sceneId: Long, openedAt: Long)

    @Query("UPDATE sessions SET deletedAt = :deletedAt WHERE id = :sessionId")
    suspend fun softDeleteById(sessionId: Long, deletedAt: Long)

    @Query("UPDATE sessions SET deletedAt = NULL WHERE id = :sessionId")
    suspend fun restoreById(sessionId: Long)

    @Query("DELETE FROM sessions WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM sessions WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffTimeMillis")
    suspend fun purgeDeletedBefore(cutoffTimeMillis: Long)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteById(sessionId: Long)
}
