package com.example.rpgaudiomixer.ui.soundscapes

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSoundscapeAudioPickerMode @Inject constructor() : SoundscapeAudioPickerMode {
    override val useSystemAudioPicker: Boolean = true
}
