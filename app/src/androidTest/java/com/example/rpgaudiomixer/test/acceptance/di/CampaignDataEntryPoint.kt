package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.app.motion.MotionSystemStateRepository
import com.example.rpgaudiomixer.app.playback.ScenePlaybackController
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.trash.SceneTrashRepository
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.domain.trash.FxTrackTrashRepository
import com.example.rpgaudiomixer.domain.trash.SoundscapeCategoryTrashRepository
import com.example.rpgaudiomixer.domain.trash.SessionTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignCoverArtSelectionRepository
import com.example.rpgaudiomixer.ui.fx.FxAudioSelectionRepository
import com.example.rpgaudiomixer.ui.sessions.SessionCoverArtSelectionRepository
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioSelectionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CampaignDataEntryPoint {
    fun campaignRepository(): CampaignRepository
    fun sessionRepository(): SessionRepository
    fun sceneRepository(): SceneRepository
    fun fxRepository(): FxRepository
    fun soundscapeRepository(): SoundscapeRepository
    fun campaignTrashRepository(): CampaignTrashRepository
    fun sessionTrashRepository(): SessionTrashRepository
    fun sceneTrashRepository(): SceneTrashRepository
    fun fxTrackTrashRepository(): FxTrackTrashRepository
    fun soundscapeCategoryTrashRepository(): SoundscapeCategoryTrashRepository
    fun trashVaultRepository(): TrashVaultRepository
    fun campaignCoverArtSelectionRepository(): CampaignCoverArtSelectionRepository
    fun sessionCoverArtSelectionRepository(): SessionCoverArtSelectionRepository
    fun fxAudioSelectionRepository(): FxAudioSelectionRepository
    fun soundscapeAudioSelectionRepository(): SoundscapeAudioSelectionRepository
    fun motionSystemStateRepository(): MotionSystemStateRepository
    fun scenePlaybackController(): ScenePlaybackController
}
