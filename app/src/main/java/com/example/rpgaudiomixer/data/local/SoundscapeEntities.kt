package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Soundscape category entity - represents a category of soundscape tracks.
 */
@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "icon_res_id")
    val iconResId: Int? = null,

    @ColumnInfo(name = "theme_label")
    val themeLabel: String? = null
)

/**
 * Soundscape track entity - represents a looping audio track within a category.
 */
@Entity(
    tableName = "soundscape_tracks",
    foreignKeys = [
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("category_id")]
)
data class SoundscapeTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "intensity_level")
    val intensityLevel: Int, // 1=I, 2=II, 3=III

    @ColumnInfo(name = "mix_volume")
    val mixVolume: Float = 0.75f,

    @ColumnInfo(name = "play_count")
    val playCount: Int = 0
)

/**
 * DAO for Soundscape Category entities.
 */
@Dao
interface SoundscapeCategoryDao {

    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Upsert
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeCategoryEntity?

    /**
     * Get count of tracks per intensity level for a category.
     */
    @Query("""
        SELECT COUNT(*) FROM soundscape_tracks
        WHERE category_id = :categoryId AND intensity_level = :intensityLevel
    """)
    suspend fun getTrackCountByIntensity(categoryId: Long, intensityLevel: Int): Int
}

/**
 * DAO for Soundscape Track entities.
 */
@Dao
interface SoundscapeTrackDao {

    @Query("SELECT * FROM soundscape_tracks WHERE category_id = :categoryId ORDER BY name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("""
        SELECT * FROM soundscape_tracks
        WHERE category_id = :categoryId AND intensity_level = :intensityLevel
        ORDER BY name ASC
    """)
    fun observeByCategoryAndIntensity(categoryId: Long, intensityLevel: Int): Flow<List<SoundscapeTrackEntity>>

    @Upsert
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Query("DELETE FROM soundscape_tracks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM soundscape_tracks WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeTrackEntity?

    @Query("UPDATE soundscape_tracks SET play_count = play_count + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)

    @Query("SELECT * FROM soundscape_tracks ORDER BY play_count DESC LIMIT 1")
    fun observeMostPlayed(): Flow<SoundscapeTrackEntity?>
}
