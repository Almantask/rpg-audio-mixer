package com.example.rpgaudiomixer.app.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue

object SettingsSyncState {
    var lastSuccessfulSyncAtMillis by mutableLongStateOf(0L)

    fun markSynced(atMillis: Long) {
        lastSuccessfulSyncAtMillis = atMillis
    }

    fun reset() {
        lastSuccessfulSyncAtMillis = 0L
    }
}
