package com.example.rpgaudiomixer.app.domain.model

enum class TrashItemType { CAMPAIGN, SESSION, SCENE }

data class TrashItem(
    val id: Long,
    val name: String,
    val type: TrashItemType,
    val deletedAt: Long
)
