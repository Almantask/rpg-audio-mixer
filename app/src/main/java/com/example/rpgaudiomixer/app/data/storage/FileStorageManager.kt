package com.example.rpgaudiomixer.app.data.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun copyToInternalStorage(uri: Uri, fileName: String): String = withContext(Dispatchers.IO) {
        val audioDir = File(context.filesDir, AUDIO_LIBRARY_DIR)
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        val targetFile = File(audioDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("Failed to open input stream for URI: $uri")
        targetFile.absolutePath
    }

    companion object {
        const val AUDIO_LIBRARY_DIR = "audio_library"
    }
}
