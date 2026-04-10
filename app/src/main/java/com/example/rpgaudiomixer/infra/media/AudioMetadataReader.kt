package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.media.MediaMetadataRetriever
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface AudioMetadataReader {
    suspend fun readDurationMillis(filePath: String): Result<Long>
}

class AndroidAudioMetadataReader @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : AudioMetadataReader {
    override suspend fun readDurationMillis(filePath: String): Result<Long> {
        return runCatching {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(appContext, android.net.Uri.parse(filePath))
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?: error("Unable to read audio duration.")
            }
        }
    }
}
