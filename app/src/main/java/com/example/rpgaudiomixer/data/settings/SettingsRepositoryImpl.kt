package com.example.rpgaudiomixer.data.settings

import android.content.SharedPreferences
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.settings.SettingsRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository,
) : SettingsRepository {
    private var currentTimeProvider: () -> Long = System::currentTimeMillis
    private val lastSuccessfulSyncAt = MutableStateFlow(sharedPreferences.readLastSuccessfulSyncAt())

    internal constructor(
        sharedPreferences: SharedPreferences,
        soundscapeRepository: SoundscapeRepository,
        fxRepository: FxRepository,
        currentTimeProvider: () -> Long,
    ) : this(
        sharedPreferences = sharedPreferences,
        soundscapeRepository = soundscapeRepository,
        fxRepository = fxRepository,
    ) {
        this.currentTimeProvider = currentTimeProvider
    }

    override fun observeLastSuccessfulSyncAt(): Flow<Long?> = lastSuccessfulSyncAt

    override suspend fun syncPurchasesAndFreeTracks() {
        if (!soundscapeRepository.observeHasDemoSoundscapes().first()) {
            soundscapeRepository.seedDemoSoundscapes()
        }
        if (!fxRepository.observeHasDemoFxTracks().first()) {
            fxRepository.seedDemoFxTracks()
        }
        val syncedAt = currentTimeProvider()
        sharedPreferences.edit().putLong(LAST_SUCCESSFUL_SYNC_AT_KEY, syncedAt).apply()
        lastSuccessfulSyncAt.value = syncedAt
    }

    private fun SharedPreferences.readLastSuccessfulSyncAt(): Long? =
        if (contains(LAST_SUCCESSFUL_SYNC_AT_KEY)) getLong(LAST_SUCCESSFUL_SYNC_AT_KEY, 0L) else null

    private companion object {
        const val LAST_SUCCESSFUL_SYNC_AT_KEY = "last_successful_sync_at"
    }
}
