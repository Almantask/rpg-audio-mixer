package com.example.rpgaudiomixer.domain.fx

interface FxPreviewPlayer {
    fun play(filePath: String)

    fun pause()

    fun stop()
}
