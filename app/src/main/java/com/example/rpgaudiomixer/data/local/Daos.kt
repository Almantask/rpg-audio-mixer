package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY lastPlayed DESC")
    fun observeAll(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CampaignEntity): Long

    @Delete
    suspend fun delete(entity: CampaignEntity)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SessionEntity): Long

    @Delete
    suspend fun delete(entity: SessionEntity)
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SceneEntity): Long

    @Delete
    suspend fun delete(entity: SceneEntity)
}

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoundscapeCategoryEntity): Long

    @Delete
    suspend fun delete(entity: SoundscapeCategoryEntity)
}

@Dao
interface IntensityLevelDao {
    @Query("SELECT * FROM intensity_levels WHERE categoryId = :categoryId ORDER BY level ASC")
    fun observeByCategory(categoryId: Long): Flow<List<IntensityLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: IntensityLevelEntity): Long

    @Delete
    suspend fun delete(entity: IntensityLevelEntity)
}

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE intensityLevelId = :intensityLevelId ORDER BY name ASC")
    fun observeByIntensityLevel(intensityLevelId: Long): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TrackEntity): Long

    @Delete
    suspend fun delete(entity: TrackEntity)
}

@Dao
interface FXDao {
    @Query("SELECT * FROM fx ORDER BY name ASC")
    fun observeAll(): Flow<List<FXEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FXEntity): Long

    @Delete
    suspend fun delete(entity: FXEntity)
}
