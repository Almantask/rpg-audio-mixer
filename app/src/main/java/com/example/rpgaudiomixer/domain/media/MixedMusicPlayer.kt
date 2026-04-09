package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.StateFlow

interface MixedMusicPlayer {
    fun playSingleSound(soundId: String)
    fun playPreview(filePath: String, title: String)
    fun pausePreview()
    fun stopPreview()
    val isPreviewing: StateFlow<Boolean>
    val currentPreviewTitle: StateFlow<String?>
}
