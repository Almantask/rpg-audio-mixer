package com.example.rpgaudiomixer.domain.model

data class ResumeScene(
    val sessionId: Long,
    val sceneId: Long,
    val sceneName: String,
    val sceneDescription: String? = null,
)
