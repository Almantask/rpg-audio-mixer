package com.example.rpgaudiomixer.test.acceptance.fakes.audio

import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

class AudioEngineWorld {
    private val trackFactory = FakeTrackFactory()
    private var nextCategoryId = 1L
    private var masterAtmospherePercent = 100
    private var soundboardMasterPercent = 100
    private var soundboardLimit = 5
    private val categories = linkedMapOf<String, CategoryState>()
    private val fxTracks = linkedMapOf<String, FxTrack>()
    private var oldestStoppedFxTrackName: String? = null
    private var oldestStoppedCategoryName: String? = null

    var sceneAudioEngine: SceneAudioEngine = SceneAudioEngine(trackFactory = trackFactory)
        private set

    var soundboardPlayer: SoundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)
        private set

    fun reset() {
        nextCategoryId = 1L
        categories.clear()
        fxTracks.clear()
        trackFactory.reset()
        masterAtmospherePercent = 100
        soundboardMasterPercent = 100
        soundboardLimit = 5
        oldestStoppedFxTrackName = null
        oldestStoppedCategoryName = null
        sceneAudioEngine.releaseAll()
        soundboardPlayer.releaseAll()
        sceneAudioEngine = SceneAudioEngine(trackFactory = trackFactory)
        soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory, maxConcurrentEffects = soundboardLimit)
    }

    fun reconfigureSoundboardLimit(limit: Int) {
        soundboardLimit = limit
        soundboardPlayer.releaseAll()
        soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory, maxConcurrentEffects = limit)
        soundboardPlayer.setMasterVolume(soundboardMasterPercent / 100f)
        oldestStoppedFxTrackName = null
    }

    fun ensureCategory(name: String): CategoryState {
        return categories.getOrPut(name) {
            CategoryState(id = nextCategoryId++, name = name)
        }
    }

    fun addCategoryWithTracks(name: String, trackNames: List<String>, intensityLevel: IntensityLevel = IntensityLevel.I): CategoryState {
        val category = ensureCategory(name)
        trackNames.forEach { trackName ->
            category.tracks += SoundscapeTrack(
                id = (category.tracks.size + 1).toLong(),
                categoryId = category.id,
                name = trackName,
                filePath = trackName.toTrackPath(),
                intensityLevel = intensityLevel,
            )
        }
        return category
    }

    fun setCategoryMix(name: String, mixVolumePercent: Int) {
        val category = ensureCategory(name)
        category.mixVolumePercent = mixVolumePercent
        sceneAudioEngine.addCategory(category.id, mixVolumePercent / 100f)
    }

    fun setCategoryIntensity(name: String, intensityLevel: IntensityLevel) {
        ensureCategory(name).intensityLevel = intensityLevel
    }

    fun setMasterAtmosphere(percent: Int) {
        masterAtmospherePercent = percent
        sceneAudioEngine.setMasterVolume(percent / 100f)
    }

    fun setSoundboardMaster(percent: Int) {
        soundboardMasterPercent = percent
        soundboardPlayer.setMasterVolume(percent / 100f)
    }

    fun playCategory(name: String): SoundscapeTrack? {
        val category = ensureCategory(name)
        val activeBefore = sceneAudioEngine.currentlyPlayingCategoryIds()
        val player = sceneAudioEngine.addCategory(category.id, category.mixVolumePercent / 100f)
        player.setMixVolume(category.mixVolumePercent / 100f)
        player.setMasterVolume(masterAtmospherePercent / 100f)
        val selectedTrack = sceneAudioEngine.rollRandomTrack(category.id, category.currentPool())
        if (selectedTrack != null) {
            category.lastPlayedTrack = selectedTrack
        }
        if (activeBefore.size >= 10 && category.id !in activeBefore) {
            val stoppedCategoryId = activeBefore.firstOrNull { it !in sceneAudioEngine.currentlyPlayingCategoryIds() }
            oldestStoppedCategoryName = categories.values.firstOrNull { state -> state.id == stoppedCategoryId }?.name
        }
        category.wasEverPlayed = selectedTrack != null || player.isPlaying.value
        return selectedTrack
    }

    fun pauseCategory(name: String) {
        sceneAudioEngine.pauseCategory(requireCategory(name).id)
    }

    fun resumeCategory(name: String) {
        sceneAudioEngine.resumeCategory(requireCategory(name).id)
    }

    fun triggerFx(name: String): Long {
        val activeBefore = soundboardPlayer.activeInstances()
        val track = fxTracks.getOrPut(name) {
            FxTrack(name = name, filePath = name.toTrackPath(), durationMs = 3_000L)
        }
        val instanceId = soundboardPlayer.triggerFx(track)
        oldestStoppedFxTrackName = activeBefore.firstOrNull()?.takeIf { activeBefore.size >= soundboardLimit }?.track?.name
        return instanceId
    }

    fun stopLatestFx(name: String) {
        val instanceId = soundboardPlayer.activeInstances().lastOrNull { instance -> instance.track.name == name }?.instanceId ?: return
        soundboardPlayer.stopFx(instanceId)
    }

    fun activeFxCount(name: String): Int = soundboardPlayer.activeInstances().count { instance -> instance.track.name == name }

    fun isCategoryPlaying(name: String): Boolean = sceneAudioEngine.categoryPlayer(requireCategory(name).id)?.isPlaying?.value == true

    fun latestPlayedTrack(name: String): SoundscapeTrack? = requireCategory(name).lastPlayedTrack

    fun categoryOutputPercent(name: String): Int {
        val player = sceneAudioEngine.categoryPlayer(requireCategory(name).id) ?: return 0
        return (player.actualVolume() * 100).toInt()
    }

    fun soundboardOutputPercent(name: String): Int {
        val player = trackFactory.latestOneShotPlayer(name.toTrackPath()) ?: return 0
        return (player.volume * 100).toInt()
    }

    fun latestSoundboardPlayer(name: String): FakeTrackPlayer? = trackFactory.latestOneShotPlayer(name.toTrackPath())

    fun latestLoopPlayer(trackName: String): FakeTrackPlayer? = trackFactory.latestLoopablePlayer(trackName.toTrackPath())

    fun oldestStoppedFxTrackName(): String? = oldestStoppedFxTrackName

    fun oldestStoppedCategoryName(): String? = oldestStoppedCategoryName

    fun allPlayingCategories(): List<String> = categories.values.filter { state -> isCategoryPlaying(state.name) }.map(CategoryState::name)

    private fun requireCategory(name: String): CategoryState = categories[name] ?: error("Unknown category '$name'")

    data class CategoryState(
        val id: Long,
        val name: String,
        var intensityLevel: IntensityLevel = IntensityLevel.I,
        var mixVolumePercent: Int = 100,
        var lastPlayedTrack: SoundscapeTrack? = null,
        var wasEverPlayed: Boolean = false,
        val tracks: MutableList<SoundscapeTrack> = mutableListOf(),
    ) {
        fun currentPool(): List<SoundscapeTrack> = tracks.filter { track -> track.intensityLevel == intensityLevel }
    }
}

private fun String.toTrackPath(): String = "track://${replace(' ', '_').lowercase()}"
