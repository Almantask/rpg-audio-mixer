package com.example.rpgaudiomixer.infra.storage.db.dao

import androidx.room.ColumnInfo

/** Flat projection used in SceneDao joins to avoid needing @Relation. */
data class SceneSoundscapeWithCategory(
    // scene_soundscapes columns
    @ColumnInfo(name = "scene_id") val sceneId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "mix_volume") val mixVolume: Float,
    @ColumnInfo(name = "active_intensity") val activeIntensity: Int,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    // soundscape_categories columns (aliased)
    @ColumnInfo(name = "cat_id") val catId: Long,
    @ColumnInfo(name = "cat_name") val catName: String,
    @ColumnInfo(name = "parent_category") val parentCategory: String,
    @ColumnInfo(name = "cat_created_at") val catCreatedAt: Long,
)

/** Flat projection for scene FX joins. */
data class SceneFxWithEffect(
    // scene_fx columns
    @ColumnInfo(name = "scene_id") val sceneId: Long,
    @ColumnInfo(name = "fx_effect_id") val fxEffectId: Long,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    // fx_effects columns (aliased)
    @ColumnInfo(name = "fx_id") val fxId: Long,
    @ColumnInfo(name = "fx_name") val fxName: String,
    @ColumnInfo(name = "track_file_path") val trackFilePath: String,
    @ColumnInfo(name = "tags") val tags: List<String>,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    @ColumnInfo(name = "play_count") val playCount: Int,
    @ColumnInfo(name = "fx_created_at") val fxCreatedAt: Long,
)
