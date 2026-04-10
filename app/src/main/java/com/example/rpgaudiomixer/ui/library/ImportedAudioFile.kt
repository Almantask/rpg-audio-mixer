package com.example.rpgaudiomixer.ui.library

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ImportedAudioFile(
    val displayName: String,
    val filePath: String,
    val durationMs: Long,
)

suspend fun importAudioFileToAppStorage(
    context: Context,
    sourceUri: Uri,
    targetFolderName: String,
): ImportedAudioFile = withContext(Dispatchers.IO) {
    val resolver = context.contentResolver
    val rawDisplayName = resolver.query(
        sourceUri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null,
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            cursor.getString(nameIndex)
        } else {
            null
        }
    } ?: "soundscape-${System.currentTimeMillis()}.mp3"

    val sanitizedFileName = rawDisplayName
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .lowercase(Locale.US)
    val targetDir = File(context.filesDir, targetFolderName).apply { mkdirs() }
    val targetFile = File(targetDir, "${System.currentTimeMillis()}-$sanitizedFileName")

    resolver.openInputStream(sourceUri)?.use { inputStream ->
        targetFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    } ?: error("Unable to read the selected audio file.")

    val durationMs = MediaMetadataRetriever().run {
        setDataSource(targetFile.absolutePath)
        val metadataDuration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        release()
        metadataDuration?.toLongOrNull() ?: 0L
    }

    ImportedAudioFile(
        displayName = rawDisplayName.substringBeforeLast('.'),
        filePath = Uri.fromFile(targetFile).toString(),
        durationMs = durationMs,
    )
}
