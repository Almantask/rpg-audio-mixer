package com.example.rpgaudiomixer.infra.db

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFXTrack
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.Track
import com.example.rpgaudiomixer.infra.db.entities.CampaignEntity
import com.example.rpgaudiomixer.infra.db.entities.FXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneFXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneSoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.SessionEntity
import com.example.rpgaudiomixer.infra.db.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.TrackEntity

// ── Campaign ──────────────────────────────────────────────────────────────────

fun CampaignEntity.toDomain() = Campaign(id, name, coverArtUri, lastPlayedAt)

fun Campaign.toEntity() = CampaignEntity(id, name, coverArtUri, lastPlayedAt)

// ── Session ───────────────────────────────────────────────────────────────────

fun SessionEntity.toDomain() = Session(id, campaignId, name, coverArtUri, date)

fun Session.toEntity() = SessionEntity(id, campaignId, name, coverArtUri, date)

// ── Scene ─────────────────────────────────────────────────────────────────────

fun SceneEntity.toDomain() = Scene(
    id = id,
    name = name,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
    masterAtmosphereVolume = masterAtmosphereVolume,
    masterSoundboardVolume = masterSoundboardVolume,
)

fun Scene.toEntity() = SceneEntity(
    id = id,
    name = name,
    tags = tags.joinToString(","),
    masterAtmosphereVolume = masterAtmosphereVolume,
    masterSoundboardVolume = masterSoundboardVolume,
)

// ── SoundscapeCategory ───────────────────────────────────────────────────────

fun SoundscapeCategoryEntity.toDomain(
    tracksByIntensity: Map<IntensityLevel, List<Track>> = emptyMap()
) = SoundscapeCategory(id, name, tracksByIntensity)

fun SoundscapeCategory.toEntity() = SoundscapeCategoryEntity(id, name)

// ── Track ─────────────────────────────────────────────────────────────────────

fun TrackEntity.toDomain() = Track(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = IntensityLevel.fromIndex(intensityLevel),
    mixVolume = mixVolume,
)

fun Track.toEntity() = TrackEntity(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = intensityLevel.index,
    mixVolume = mixVolume,
)

// ── FXTrack ───────────────────────────────────────────────────────────────────

fun FXTrackEntity.toDomain() = FXTrack(
    id = id,
    name = name,
    filePath = filePath,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
)

fun FXTrack.toEntity() = FXTrackEntity(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.joinToString(","),
)

// ── SceneSoundscapeCategory ───────────────────────────────────────────────────

fun SceneSoundscapeCategoryEntity.toDomain(category: SoundscapeCategory) = SceneSoundscapeCategory(
    id = id,
    sceneId = sceneId,
    category = category,
    mixVolume = mixVolume,
    sortOrder = sortOrder,
)

// ── SceneFXTrack ──────────────────────────────────────────────────────────────

fun SceneFXTrackEntity.toDomain(fxTrack: FXTrack) = SceneFXTrack(
    id = id,
    sceneId = sceneId,
    fxTrack = fxTrack,
    sortOrder = sortOrder,
)
