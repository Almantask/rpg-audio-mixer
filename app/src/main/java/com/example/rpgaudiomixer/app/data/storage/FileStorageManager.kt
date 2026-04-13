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
    /**
     * Copies a content URI to internal storage and returns the file:// path.
     * The file is stored at `context.filesDir/audio/<displayName>`.
     */
    suspend fun copyToInternalStorage(uri: Uri, displayName: String): String = withContext(Dispatchers.IO) {
        val audioDir = File(context.filesDir, "audio").also { it.mkdirs() }
        val destFile = File(audioDir, displayName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        "file://${destFile.absolutePath}"
    }
}
