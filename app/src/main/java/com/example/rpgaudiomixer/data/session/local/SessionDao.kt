package com.example.rpgaudiomixer.data.session.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query(
        """
        SELECT sessions.id, sessions.campaignId, sessions.name, sessions.dateMillis, sessions.coverArtUri,
               COUNT(session_scene_cross_refs.sceneId) AS sceneCount
        FROM sessions
        LEFT JOIN session_scene_cross_refs ON sessions.id = session_scene_cross_refs.sessionId
        WHERE sessions.campaignId = :campaignId
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
        GROUP BY sessions.id
        """,
    )
    fun observeSession(sessionId: Long): Flow<SessionWithSceneCount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SessionEntity): Long

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteById(sessionId: Long)
}
