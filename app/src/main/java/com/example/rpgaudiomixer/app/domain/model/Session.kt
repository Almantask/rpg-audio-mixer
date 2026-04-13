package com.example.rpgaudiomixer.app.domain.model

data class Session(
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val coverArtUri: String? = null,
    val date: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
