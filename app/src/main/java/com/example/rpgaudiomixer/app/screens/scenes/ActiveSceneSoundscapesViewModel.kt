package com.example.rpgaudiomixer.app.screens.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveSceneSoundscapesUiState(
    val sceneId: Long = 0,
    val categories: List<SceneSoundscapeCategory> = emptyList(),
    val masterVolume: Float = 1.0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val sessionRepository: SessionRepository,
    private val campaignRepository: CampaignRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val audioEngine: SceneAudioEngine,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sceneId: Long = savedStateHandle.get<Long>("sceneId") ?: 0
    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: -1
    private val autoplay: Boolean = savedStateHandle.get<String>("autoplay")?.toBoolean() ?: false
    private var hasAutoplayed = false

    init {
        // Update historical stats
        viewModelScope.launch {
            if (sessionId != -1L) {
                sessionRepository.updateLastOpenedScene(sessionId, sceneId)
                // To update campaign's lastPlayedAt, we need the campaignId.
            }

            // For now, if we have a session, we can find its campaign.
            // But let's just update the most recent campaign as a fallback if sessionId is provided.
            campaignRepository.observeLatest().firstOrNull()?.let { campaign ->
                campaignRepository.updateLastPlayed(campaign.id)
            }
        }
    }

    private val _masterVolume = MutableStateFlow(1.0f)
    
    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = combine(
        sceneRepository.observeCategoriesByScene(sceneId),
        _masterVolume
    ) { categories, masterVol ->
        ActiveSceneSoundscapesUiState(
            sceneId = sceneId,
            categories = categories,
            masterVolume = masterVol,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActiveSceneSoundscapesUiState()
    )

    val allCategories: StateFlow<List<SoundscapeCategory>> = soundscapeRepository.observeAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categoryPlayCounts: StateFlow<Map<Long, Int>> = soundscapeRepository.observeCategoryPlayCounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Maps categoryId to its current playing track name for UI display
    private val _playingTracks = MutableStateFlow<Map<Long, String?>>(emptyMap())
    val playingTracks: StateFlow<Map<Long, String?>> = _playingTracks.asStateFlow()

    // Maps categoryId to its isPlaying status from the engine
    private val _categoryPlayingState = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val categoryPlayingState: StateFlow<Map<Long, Boolean>> = _categoryPlayingState.asStateFlow()

    init {
        // Initialize engine with master volume
        audioEngine.setMasterVolume(_masterVolume.value)
        
        // Listen to engine state for each category
        viewModelScope.launch {
            uiState.collectLatest { state ->
                state.categories.forEach { sceneCat ->
                    val player = audioEngine.getPlayer(sceneCat.category.id)
                    // Update engine with initial mix volume from DB
                    player.setMixVolume(sceneCat.mixVolume)
                    
                    launch {
                        player.isPlaying.collect { playing ->
                            _categoryPlayingState.update { it + (sceneCat.category.id to playing) }
                        }
                    }
                    launch {
                        player.currentTrack.collect { track ->
                            _playingTracks.update { it + (sceneCat.category.id to track?.name) }
                        }
                    }
                }
                
                if (autoplay && !hasAutoplayed && state.categories.isNotEmpty()) {
                    hasAutoplayed = true
                    startPlayback()
                }
            }
        }
    }

    fun startPlayback() {
        viewModelScope.launch {
            val categories = uiState.value.categories
            val tracksToPlay = mutableMapOf<Long, SoundscapeTrack>()
            
            categories.forEach { sceneCat ->
                val tracks = soundscapeRepository.observeTracksByCategory(sceneCat.category.id).first()
                val filteredTracks = tracks.filter { it.intensityLevel == sceneCat.intensityLevel }
                if (filteredTracks.isNotEmpty()) {
                    tracksToPlay[sceneCat.category.id] = filteredTracks.random()
                }
            }
            
            audioEngine.crossfadeToScene(viewModelScope, categories, tracksToPlay)
        }
    }

    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume
        audioEngine.setMasterVolume(volume)
    }

    fun toggleCategoryPlayback(categoryId: Long) {
        val sceneCat = uiState.value.categories.find { it.category.id == categoryId } ?: return
        val player = audioEngine.getPlayer(categoryId)
        
        if (player.isPlaying.value) {
            player.stop()
        } else {
            rollRandomTrack(sceneCat)
        }
    }

    fun rollRandom(categoryId: Long) {
        val sceneCat = uiState.value.categories.find { it.category.id == categoryId } ?: return
        rollRandomTrack(sceneCat)
    }

    private fun rollRandomTrack(sceneCat: SceneSoundscapeCategory) {
        viewModelScope.launch {
            val tracks = soundscapeRepository.observeTracksByCategory(sceneCat.category.id).first()
            val filteredTracks = tracks.filter { it.intensityLevel == sceneCat.intensityLevel }
            if (filteredTracks.isNotEmpty()) {
                val player = audioEngine.getPlayer(sceneCat.category.id)
                player.playTrack(filteredTracks.random())
            }
        }
    }

    fun setMixVolume(categoryId: Long, volume: Float) {
        val player = audioEngine.getPlayer(categoryId)
        player.setMixVolume(volume)
        viewModelScope.launch {
            sceneRepository.updateSceneCategoryMixVolume(sceneId, categoryId, volume)
        }
    }

    fun setIntensity(categoryId: Long, intensity: IntensityLevel) {
        viewModelScope.launch {
            sceneRepository.updateSceneCategoryIntensity(sceneId, categoryId, intensity)
            
            // If currently playing, we should switch to a random track of the new intensity
            val player = audioEngine.getPlayer(categoryId)
            if (player.isPlaying.value) {
                val tracks = soundscapeRepository.observeTracksByCategory(categoryId).first()
                val filteredTracks = tracks.filter { it.intensityLevel == intensity }
                if (filteredTracks.isNotEmpty()) {
                    player.playTrack(filteredTracks.random())
                }
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        audioEngine.removeCategory(categoryId)
        viewModelScope.launch {
            sceneRepository.removeCategoryFromScene(sceneId, categoryId)
        }
    }

    fun addCategory(categoryId: Long) {
        val maxOrder = uiState.value.categories.maxOfOrNull { it.displayOrder } ?: -1
        viewModelScope.launch {
            sceneRepository.addCategoryToScene(sceneId, categoryId, maxOrder + 1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // We don't releaseAll() here because we want audio to continue if the user stays in the app
        // But we might want to stop if the whole activity is destroyed, handled elsewhere.
    }
}
