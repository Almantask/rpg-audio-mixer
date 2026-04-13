package com.example.rpgaudiomixer.app.domain.model

data class Scene(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
