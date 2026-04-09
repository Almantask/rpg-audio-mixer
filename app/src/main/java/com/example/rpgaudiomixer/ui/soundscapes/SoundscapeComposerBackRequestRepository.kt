package com.example.rpgaudiomixer.ui.soundscapes

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundscapeComposerBackRequestRepository @Inject constructor() {
    private val _requests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val requests = _requests.asSharedFlow()

    fun requestBack() {
        _requests.tryEmit(Unit)
    }
}
