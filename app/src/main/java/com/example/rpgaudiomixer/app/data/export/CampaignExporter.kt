package com.example.rpgaudiomixer.app.data.export

import android.content.Context
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.model.Scene
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class CampaignManifest(
    val campaignName: String,
    val scenes: List<SceneManifest>,
)

@Serializable
data class SceneManifest(
    val name: String,
    val description: String? = null,
    val tags: String? = null,
    val notes: String? = null,
)

@Singleton
class CampaignExporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun export(
        campaign: Campaign,
        scenes: List<Scene>,
        audioFiles: List<String>,
        outputStream: OutputStream,
    ) {
        ZipOutputStream(outputStream).use { zip ->
            // Write manifest
            val manifest = CampaignManifest(
                campaignName = campaign.name,
                scenes = scenes.map { scene ->
                    SceneManifest(
                        name = scene.name,
                        description = scene.description,
                        tags = scene.tags,
                        notes = scene.notes,
                    )
                },
            )
            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(Json.encodeToString(CampaignManifest.serializer(), manifest).toByteArray())
            zip.closeEntry()

            // Write audio files
            audioFiles.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    zip.putNextEntry(ZipEntry("audio/${file.name}"))
                    file.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
            }
        }
    }
}
