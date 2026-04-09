package com.example.rpgaudiomixer.ui.fx

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFxAudioPickerMode @Inject constructor() : FxAudioPickerMode {
    override val useSystemAudioPicker: Boolean = true
}
