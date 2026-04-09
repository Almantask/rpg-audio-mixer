package com.example.rpgaudiomixer.data.session.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class SessionListItemRow(
    val id: Long,
    val campaignId: Long,
    val name: String,
    val dateMillis: Long,
    val coverArtUri: String?,
    val sceneCount: Int,
)

data class ResumeSceneRow(
    val sessionId: Long,
    val sceneId: Long,
    val sceneName: String,
    val sceneDescription: String?,
)

@Dao
interface SessionDao {
    @Query(
        """
        SELECT sessions.id,
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
        ORDER BY sessions.dateMillis DESC, sessions.id DESC
        """,
    )
    fun observeByCampaign(campaignId: Long): Flow<List<SessionListItemRow>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1")
    fun observeById(sessionId: Long): Flow<SessionEntity?>

    @Query(
        """
        SELECT sessions.id AS sessionId,
               scenes.id AS sceneId,
               scenes.name AS sceneName,
               scenes.description AS sceneDescription
        FROM sessions
        INNER JOIN scenes ON scenes.id = sessions.lastOpenedSceneId
        WHERE sessions.campaignId = :campaignId
          AND sessions.lastOpenedSceneId IS NOT NULL
        ORDER BY sessions.lastOpenedAtMillis DESC, sessions.id DESC
        LIMIT 1
        """,
    )
    fun observeLastOpenedScene(campaignId: Long): Flow<ResumeSceneRow?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity): Long

    @Query(
        """
        UPDATE sessions
        SET lastOpenedSceneId = :sceneId,
            lastOpenedAtMillis = :openedAtMillis
        WHERE id = :sessionId
        """,
    )
    suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long, openedAtMillis: Long)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteById(sessionId: Long)

    @Query("DELETE FROM sessions")
    suspend fun clearAll()
}
