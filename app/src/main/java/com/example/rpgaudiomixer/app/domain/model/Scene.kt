package com.example.rpgaudiomixer.app.domain.model

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
