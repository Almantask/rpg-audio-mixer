package com.example.rpgaudiomixer.ui.campaigns

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCampaignPhotoPickerMode @Inject constructor() : CampaignPhotoPickerMode {
    override val useSystemPhotoPicker: Boolean = true
}
