package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.ui.fx.FxAudioPickerMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeFxAudioPickerMode @Inject constructor() : FxAudioPickerMode {
    override val useSystemAudioPicker: Boolean = false
}
