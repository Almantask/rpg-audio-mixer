package com.example.rpgaudiomixer.domain.model

data class TrashItem(
    val id: Long,
    val title: String,
    val type: TrashItemType,
    val deletedAt: Long,
)

enum class TrashItemType {
    CAMPAIGN,
    SESSION,
    SCENE,
    SOUNDSCAPE,
    FX,
}
