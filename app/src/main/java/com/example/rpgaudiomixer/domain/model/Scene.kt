package com.example.rpgaudiomixer.domain.model

import java.time.Instant

data class Scene(
    val id: String,
    val name: String,
    val campaignId: String,
    val lastOpenedAt: Instant?
)
