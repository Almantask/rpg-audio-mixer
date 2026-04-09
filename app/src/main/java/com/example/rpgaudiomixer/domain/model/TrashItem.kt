package com.example.rpgaudiomixer.domain.model

sealed class TrashItem {
    abstract val id: Long
    abstract val name: String
    abstract val deletedAt: Long
    abstract val type: TrashItemType

    data class Campaign(
        override val id: Long,
        override val name: String,
        override val deletedAt: Long,
        val coverArtUri: String?
    ) : TrashItem() {
        override val type = TrashItemType.CAMPAIGN
    }

    data class Session(
        override val id: Long,
        override val name: String,
        override val deletedAt: Long,
        val campaignId: Long
    ) : TrashItem() {
        override val type = TrashItemType.SESSION
    }

    data class Scene(
        override val id: Long,
        override val name: String,
        override val deletedAt: Long,
        val description: String?
    ) : TrashItem() {
        override val type = TrashItemType.SCENE
    }

    data class SoundscapeCategory(
        override val id: Long,
        override val name: String,
        override val deletedAt: Long
    ) : TrashItem() {
        override val type = TrashItemType.SOUNDSCAPE
    }

    data class FxTrack(
        override val id: Long,
        override val name: String,
        override val deletedAt: Long
    ) : TrashItem() {
        override val type = TrashItemType.FX
    }
}

enum class TrashItemType {
    CAMPAIGN,
    SESSION,
    SCENE,
    SOUNDSCAPE,
    FX
}
