package com.example.rpgaudiomixer.infra.session

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(session: SessionEntity): Long

    @Query("DELETE FROM sessions WHERE id = :id")
    fun delete(id: Long): Int
}
