package com.example.rpgaudiomixer.app.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
