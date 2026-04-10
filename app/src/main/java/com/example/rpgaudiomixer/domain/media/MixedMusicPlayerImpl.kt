package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.storage.TrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MixedMusicPlayerImpl(
    private val trackFactory: TrackFactory,
    private val trackRepository: TrackRepository
) : MixedMusicPlayer {

    private var previewPlayer: TrackPlayer? = null
    
    private val _isPreviewing = MutableStateFlow(false)
    override val isPreviewing: StateFlow<Boolean> = _isPreviewing.asStateFlow()
    
    private val _currentPreviewTitle = MutableStateFlow<String?>(null)
    override val currentPreviewTitle: StateFlow<String?> = _currentPreviewTitle.asStateFlow()

    override fun playSingleSound(soundId: String) {
        val path = trackRepository.getTrackFilePath(soundId)
        val player = trackFactory.createOneTimeTrackPlayer(path)
        player.play()
    }

    override fun playPreview(filePath: String, title: String) {
        if (_currentPreviewTitle.value == title && previewPlayer != null) {
            previewPlayer?.play()
            _isPreviewing.value = true
            return
        }

        stopPreview()
        
        previewPlayer = trackFactory.createOneTimeTrackPlayer(filePath)
        _currentPreviewTitle.value = title
        _isPreviewing.value = true
        previewPlayer?.play()
    }

    override fun pausePreview() {
        previewPlayer?.pause()
        _isPreviewing.value = false
    }

    override fun stopPreview() {
        previewPlayer?.stop()
        previewPlayer = null
        _isPreviewing.value = false
        _currentPreviewTitle.value = null
    }
}
