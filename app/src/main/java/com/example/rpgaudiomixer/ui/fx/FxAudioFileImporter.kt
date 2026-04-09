package com.example.rpgaudiomixer.ui.fx

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FxAudioFileImporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun importAudio(uri: Uri): PickedFxAudio? {
        val mimeType = context.contentResolver.getType(uri)
        if (mimeType != null && !mimeType.startsWith("audio/")) {
            return null
        }

        val fileName = resolveDisplayName(uri) ?: return null
        val targetDirectory = File(context.filesDir, "fx").apply { mkdirs() }
        val targetFile = File(targetDirectory, "${System.currentTimeMillis()}_$fileName")

        context.contentResolver.openInputStream(uri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null

        val durationMs = runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, Uri.fromFile(targetFile))
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            } finally {
                retriever.release()
            }
        }.getOrDefault(0L)

        if (durationMs <= 0L) {
            targetFile.delete()
            return null
        }

        return PickedFxAudio(
            displayName = fileName,
            filePath = targetFile.toURI().toString(),
            durationMs = durationMs,
            isValidAudio = true,
        )
    }

    private fun resolveDisplayName(uri: Uri): String? {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(0)
            }
        }
        return uri.lastPathSegment?.substringAfterLast('/')
    }
}
