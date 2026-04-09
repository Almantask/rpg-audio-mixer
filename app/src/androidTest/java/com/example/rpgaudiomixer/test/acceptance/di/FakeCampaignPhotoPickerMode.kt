package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.ui.campaigns.CampaignPhotoPickerMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeCampaignPhotoPickerMode @Inject constructor() : CampaignPhotoPickerMode {
    override val useSystemPhotoPicker: Boolean = false
}
