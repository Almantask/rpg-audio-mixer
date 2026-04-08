package com.example.rpgaudiomixer.data.fx

import android.content.Context
import android.media.MediaMetadataRetriever
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
class AndroidFxAudioImporter @Inject constructor(
    @ApplicationContext private val context: Context,
) : FxAudioImporter {

    override suspend fun importAudio(sourceUri: String): ImportedFxAudioFile = withContext(Dispatchers.IO) {
        val uri = sourceUri.toUri()
        val displayName = uri.resolveDisplayName().sanitizeFileName()
        val durationMs = uri.resolveDurationMs()
        require(durationMs > 0L) { "The file could not be read as audio." }

        val targetDirectory = File(context.filesDir, "fx").apply { mkdirs() }
        val targetFile = File(targetDirectory, "${UUID.randomUUID()}-$displayName")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            targetFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: error("The file could not be read as audio.")

        ImportedFxAudioFile(
            displayName = displayName,
            storedPath = Uri.fromFile(targetFile).toString(),
            durationMs = durationMs,
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
            ?: "effect.mp3"

    private fun Uri.resolveDurationMs(): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, this)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            retriever.release()
        }
    }

    private fun String.sanitizeFileName(): String =
        replace("/", "_")
            .replace("\\", "_")
            .ifBlank { "effect.mp3" }
}
