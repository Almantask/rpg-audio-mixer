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
        SELECT
            sessions.id,
            sessions.campaignId,
            sessions.name,
            sessions.dateMillis,
            sessions.coverArtUri,
            COUNT(session_scene_cross_refs.sceneId) AS sceneCount
        FROM sessions
        LEFT JOIN session_scene_cross_refs
            ON sessions.id = session_scene_cross_refs.sessionId
        WHERE sessions.campaignId = :campaignId
        GROUP BY sessions.id
        ORDER BY sessions.dateMillis DESC
        """,
    )
    fun observeByCampaign(campaignId: Long): Flow<List<SessionListItemEntity>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getById(sessionId: Long): SessionEntity?

    @Query(
        """
        SELECT scenes.*
        FROM sessions
        INNER JOIN scenes
            ON scenes.id = sessions.lastOpenedSceneId
        WHERE sessions.campaignId = :campaignId
          AND sessions.lastOpenedSceneId IS NOT NULL
        ORDER BY sessions.dateMillis DESC
        LIMIT 1
        """,
    )
    fun observeResumeScene(campaignId: Long): Flow<SceneEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SessionEntity): Long

    @Query("UPDATE sessions SET lastOpenedSceneId = :sceneId WHERE id = :sessionId")
    suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun delete(sessionId: Long)
}
