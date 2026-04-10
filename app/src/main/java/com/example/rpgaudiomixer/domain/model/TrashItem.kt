package com.example.rpgaudiomixer.domain.model

enum class TrashItemType {
    CAMPAIGN,
    SESSION,
    SCENE,
    SOUNDSCAPE,
    FX,
}

data class TrashItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val deletedAt: Long,
    val type: TrashItemType,
)
