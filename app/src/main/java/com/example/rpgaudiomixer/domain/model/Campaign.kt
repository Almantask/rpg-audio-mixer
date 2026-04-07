package com.example.rpgaudiomixer.domain.model

import java.time.Instant

data class Campaign(
    val id: String,
    val name: String,
    val lastPlayedAt: Instant?
)
