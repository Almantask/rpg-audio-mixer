package com.example.rpgaudiomixer.app.ui

import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.model.SoundEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.storage.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    fun getCategories(): List<SoundscapeCategory> = repository.getAllSoundscapeCategories()

    fun getEffects(): List<SoundEffect> = repository.getAllSoundEffects()

    fun addSampleCategory() {
        repository.addSoundscapeCategory(SoundscapeCategory(name = "Weather", intensityLevel = 1))
    }

    fun addSampleEffect() {
        repository.addSoundEffect(SoundEffect(name = "Thunder", trackId = "thunder"))
    }

    fun updateCategory(category: SoundscapeCategory) {
        repository.updateSoundscapeCategory(category)
    }

    fun updateEffect(effect: SoundEffect) {
        repository.updateSoundEffect(effect)
    }
}
