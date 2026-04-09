package com.example.rpgaudiomixer.app.screens

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsSyncRepository @Inject constructor() {
    private val _lastSuccessfulSyncAtMillis = MutableStateFlow<Long?>(null)
    val lastSuccessfulSyncAtMillis: StateFlow<Long?> = _lastSuccessfulSyncAtMillis.asStateFlow()

    fun markSynced(atMillis: Long) {
        _lastSuccessfulSyncAtMillis.value = atMillis
    }

    fun setLastSuccessfulSyncAtMillis(atMillis: Long?) {
        _lastSuccessfulSyncAtMillis.value = atMillis
    }

    fun reset() {
        _lastSuccessfulSyncAtMillis.value = null
    }
}
