
package com.example.rpgaudiomixer.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

fun prepopulateDatabase(context: Context, scope: CoroutineScope, db: AppDatabase) {
    scope.launch {
        if (db.campaignDao().observeAll().valueOrNull().isNullOrEmpty()) {
            db.campaignDao().upsert(CampaignEntity(name = "Sample Campaign", coverArtUri = null, lastPlayed = System.currentTimeMillis()))
        }
        // Add more prepopulate logic as needed
    }
}

private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.valueOrNull(): T? =
    try { this.firstOrNull() } catch (_: Exception) { null }
