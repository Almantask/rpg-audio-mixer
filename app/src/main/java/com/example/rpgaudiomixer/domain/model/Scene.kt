package com.example.rpgaudiomixer.domain.model

data class Scene(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList()
) {
    fun tagsAsString(): String = tags.joinToString(",")

    companion object {
        fun fromTagsString(id: Long, name: String, description: String?, tagsString: String): Scene {
            val tagsList = if (tagsString.isBlank()) {
                emptyList()
            } else {
                tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }
            return Scene(id, name, description, tagsList)
        }
    }
}
