package com.example.rpgaudiomixer.infra.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rpgaudiomixer.infra.storage.db.entity.FxEffectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FxDao {
    @Query("SELECT * FROM fx_effects ORDER BY created_at ASC")
    fun getAllEffects(): Flow<List<FxEffectEntity>>

    @Query("SELECT * FROM fx_effects WHERE id = :id")
    fun getEffectById(id: Long): Flow<FxEffectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(effect: FxEffectEntity): Long

    @Update
    suspend fun update(effect: FxEffectEntity)

    @Delete
    suspend fun delete(effect: FxEffectEntity)

    @Query("UPDATE fx_effects SET play_count = play_count + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)
}
