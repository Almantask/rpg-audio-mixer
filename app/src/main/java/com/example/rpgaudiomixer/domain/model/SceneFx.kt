package com.example.rpgaudiomixer.domain.model

/** Represents an FX effect attached to a scene's soundboard. */
data class SceneFx(
    val sceneId: Long,
    val effect: FxEffect,
    val order: Int = 0,
)
