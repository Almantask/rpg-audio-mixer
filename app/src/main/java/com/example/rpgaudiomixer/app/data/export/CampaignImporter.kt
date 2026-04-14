package com.example.rpgaudiomixer.app.data.export

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

data class ImportedCampaign(
    val manifest: CampaignManifest,
    val audioFiles: Map<String, File>,
)

@Singleton
class CampaignImporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun import(inputStream: InputStream): ImportedCampaign {
        var manifest: CampaignManifest? = null
        val audioFiles = mutableMapOf<String, File>()
        val importDir = File(context.filesDir, "import_temp")
        importDir.mkdirs()

        ZipInputStream(inputStream).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                when {
                    entry.name == "manifest.json" -> {
                        val json = zip.readBytes().toString(Charsets.UTF_8)
                        manifest = Json.decodeFromString(CampaignManifest.serializer(), json)
                    }
                    entry.name.startsWith("audio/") -> {
                        val fileName = entry.name.removePrefix("audio/")
                        val outFile = File(importDir, fileName)
                        outFile.outputStream().use { zip.copyTo(it) }
                        audioFiles[fileName] = outFile
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        return ImportedCampaign(
            manifest = manifest ?: error("No manifest found in archive"),
            audioFiles = audioFiles,
        )
    }
}
