package com.example.rpgaudiomixer.app.ui.credits

import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.session.Session

sealed class TrashItem {
    abstract val id: Long
    abstract val name: String
    abstract val deletedAt: Long
    abstract val typeName: String

    data class CampaignItem(val campaign: Campaign) : TrashItem() {
        override val id = campaign.id
        override val name = campaign.name
        override val deletedAt = campaign.deletedAt ?: 0L
        override val typeName = "Campaign"
    }

    data class SessionItem(val session: Session) : TrashItem() {
        override val id = session.id
        override val name = session.name
        override val deletedAt = session.deletedAt ?: 0L
        override val typeName = "Session"
    }

    data class SceneItem(val scene: Scene) : TrashItem() {
        override val id = scene.id
        override val name = scene.name
        override val deletedAt = scene.deletedAt ?: 0L
        override val typeName = "Scene"
    }

    data class CategoryItem(val category: SoundscapeCategory) : TrashItem() {
        override val id = category.id
        override val name = category.name
        override val deletedAt = category.deletedAt ?: 0L
        override val typeName = "Atmosphere"
    }

    data class FxItem(val fx: FxTrack) : TrashItem() {
        override val id = fx.id
        override val name = fx.name
        override val deletedAt = fx.deletedAt ?: 0L
        override val typeName = "Sound Effect"
    }
}
