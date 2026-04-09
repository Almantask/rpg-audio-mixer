package com.example.rpgaudiomixer.app.settings

class SettingsSyncPolicy(
    private val cooldownMillis: Long = COOLDOWN_MILLIS,
) {
    fun isSyncAvailable(
        currentTimeMillis: Long,
        lastSyncedAtMillis: Long?,
    ): Boolean {
        return lastSyncedAtMillis == null ||
            currentTimeMillis - lastSyncedAtMillis >= cooldownMillis
    }

    companion object {
        const val COOLDOWN_MILLIS: Long = 24 * 60 * 60 * 1000L
    }
}
