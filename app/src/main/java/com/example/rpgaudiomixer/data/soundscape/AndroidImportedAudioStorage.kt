package com.example.rpgaudiomixer.data.soundscape

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AndroidImportedAudioStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) : ImportedAudioStorage {

    override suspend fun importAudio(sourceUri: String): ImportedAudioFile = withContext(Dispatchers.IO) {
        val uri = sourceUri.toUri()
        val fileName = uri.resolveDisplayName().sanitizeFileName()
        val targetDirectory = File(context.filesDir, "soundscapes").apply { mkdirs() }
        val targetFile = File(targetDirectory, "${UUID.randomUUID()}-$fileName")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            targetFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: error("Unable to open audio source: $sourceUri")

        ImportedAudioFile(
            displayName = fileName,
            storedPath = targetFile.absolutePath,
        )
    }

    private fun Uri.resolveDisplayName(): String =
        context.contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    cursor.getString(nameIndex)
                } else {
                    lastPathSegment
                }
            }
            ?: lastPathSegment
            ?: "soundscape.mp3"

    private fun String.sanitizeFileName(): String =
        replace("/", "_")
            .replace("\\", "_")
            .ifBlank { "soundscape.mp3" }
}
