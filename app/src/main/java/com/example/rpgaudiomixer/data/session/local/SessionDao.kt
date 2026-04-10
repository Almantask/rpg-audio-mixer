package com.example.rpgaudiomixer.data.session.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query(
        """
        SELECT sessions.id, sessions.campaignId, sessions.name, sessions.dateMillis, sessions.coverArtUri,
               COUNT(session_scene_cross_refs.sceneId) AS sceneCount
        FROM sessions
        INNER JOIN campaigns ON campaigns.id = sessions.campaignId
        LEFT JOIN session_scene_cross_refs ON sessions.id = session_scene_cross_refs.sessionId
        WHERE sessions.campaignId = :campaignId
          AND sessions.deletedAt IS NULL
          AND campaigns.deletedAt IS NULL
        GROUP BY sessions.id
        ORDER BY sessions.dateMillis DESC, sessions.id DESC
        """,
    )
    fun observeByCampaign(campaignId: Long): Flow<List<SessionWithSceneCount>>

    @Query(
        """
        SELECT sessions.id, sessions.campaignId, sessions.name, sessions.dateMillis, sessions.coverArtUri,
               COUNT(session_scene_cross_refs.sceneId) AS sceneCount
        FROM sessions
        LEFT JOIN session_scene_cross_refs ON sessions.id = session_scene_cross_refs.sessionId
        WHERE sessions.id = :sessionId
          AND sessions.deletedAt IS NULL
        GROUP BY sessions.id
        """,
    )
    fun observeSession(sessionId: Long): Flow<SessionWithSceneCount?>

    @Query(
        """
        SELECT scenes.*
        FROM sessions
        INNER JOIN scenes ON scenes.id = sessions.lastOpenedSceneId
        WHERE sessions.campaignId = :campaignId
          AND sessions.lastOpenedSceneId IS NOT NULL
          AND sessions.deletedAt IS NULL
          AND scenes.deletedAt IS NULL
        ORDER BY sessions.lastSceneOpenedAt DESC, sessions.id DESC
        LIMIT 1
        """,
    )
    fun observeLastOpenedSceneInCampaign(campaignId: Long): Flow<SceneEntity?>

    @Query("SELECT * FROM sessions WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SessionEntity): Long

    @Query(
        """
        UPDATE sessions
        SET lastOpenedSceneId = :sceneId,
            lastSceneOpenedAt = :openedAtMillis
        WHERE id = :sessionId
        """,
    )
    suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long, openedAtMillis: Long)

    @Query("UPDATE sessions SET deletedAt = :deletedAt WHERE id = :sessionId")
    suspend fun softDeleteById(sessionId: Long, deletedAt: Long)

    @Query("UPDATE sessions SET deletedAt = NULL WHERE id = :sessionId")
    suspend fun restore(sessionId: Long)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun hardDeleteById(sessionId: Long)

    @Query("DELETE FROM sessions WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM sessions WHERE deletedAt IS NOT NULL AND deletedAt <= :cutoffMillis")
    suspend fun purgeDeletedBefore(cutoffMillis: Long)
}
