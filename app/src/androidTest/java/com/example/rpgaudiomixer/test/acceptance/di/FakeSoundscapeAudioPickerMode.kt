package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioPickerMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeSoundscapeAudioPickerMode @Inject constructor() : SoundscapeAudioPickerMode {
    override val useSystemAudioPicker: Boolean = false
}
