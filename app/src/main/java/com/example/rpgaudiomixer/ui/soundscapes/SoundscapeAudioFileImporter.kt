package com.example.rpgaudiomixer.ui.soundscapes

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundscapeAudioFileImporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun importAudio(uri: Uri): Pair<String, String>? {
        val fileName = resolveDisplayName(uri) ?: return null
        val targetDirectory = File(context.filesDir, "soundscapes").apply { mkdirs() }
        val targetFile = File(targetDirectory, "${System.currentTimeMillis()}_$fileName")

        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            fileName to targetFile.absolutePath
        } catch (_: Exception) {
            targetFile.delete()
            null
        }
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
