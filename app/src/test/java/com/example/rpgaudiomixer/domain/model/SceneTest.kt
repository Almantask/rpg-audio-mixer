package com.example.rpgaudiomixer.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneTest {

    @Test
    fun `Scene with all fields creates correctly`() {
        // Arrange
        val id = 1L
        val name = "Tavern Encounter"
        val description = "A crowded tavern with mysterious NPCs"
        val tags = listOf("tavern", "social", "city")

        // Act
        val scene = Scene(
            id = id,
            name = name,
            description = description,
            tags = tags
        )

        // Assert
        assertThat(scene.id).isEqualTo(id)
        assertThat(scene.name).isEqualTo(name)
        assertThat(scene.description).isEqualTo(description)
        assertThat(scene.tags).containsExactly("tavern", "social", "city")
    }

    @Test
    fun `Scene with null description creates correctly`() {
        // Arrange
        val name = "Forest Path"

        // Act
        val scene = Scene(name = name, description = null, tags = emptyList())

        // Assert
        assertThat(scene.name).isEqualTo(name)
        assertThat(scene.description).isNull()
    }

    @Test
    fun `Scene with empty tags creates correctly`() {
        // Arrange
        val name = "Empty Scene"

        // Act
        val scene = Scene(name = name)

        // Assert
        assertThat(scene.tags).isEmpty()
    }

    @Test
    fun `tagsAsString returns comma-separated tags`() {
        // Arrange
        val scene = Scene(
            name = "Combat Scene",
            tags = listOf("combat", "dungeon", "boss")
        )

        // Act
        val tagsString = scene.tagsAsString()

        // Assert
        assertThat(tagsString).isEqualTo("combat,dungeon,boss")
    }

    @Test
    fun `tagsAsString returns empty string for empty tags`() {
        // Arrange
        val scene = Scene(name = "Scene", tags = emptyList())

        // Act
        val tagsString = scene.tagsAsString()

        // Assert
        assertThat(tagsString).isEmpty()
    }

    @Test
    fun `fromTagsString parses comma-separated tags correctly`() {
        // Arrange
        val tagsString = "forest,exploration,magic"

        // Act
        val scene = Scene.fromTagsString(
            id = 1L,
            name = "Forest",
            description = "A magical forest",
            tagsString = tagsString
        )

        // Assert
        assertThat(scene.tags).containsExactly("forest", "exploration", "magic")
    }

    @Test
    fun `fromTagsString handles empty string`() {
        // Arrange
        val tagsString = ""

        // Act
        val scene = Scene.fromTagsString(
            id = 1L,
            name = "Scene",
            description = null,
            tagsString = tagsString
        )

        // Assert
        assertThat(scene.tags).isEmpty()
    }

    @Test
    fun `fromTagsString trims whitespace from tags`() {
        // Arrange
        val tagsString = " tavern , city , social "

        // Act
        val scene = Scene.fromTagsString(
            id = 1L,
            name = "Scene",
            description = null,
            tagsString = tagsString
        )

        // Assert
        assertThat(scene.tags).containsExactly("tavern", "city", "social")
    }

    @Test
    fun `fromTagsString filters out empty tags`() {
        // Arrange
        val tagsString = "combat,,dungeon,,"

        // Act
        val scene = Scene.fromTagsString(
            id = 1L,
            name = "Scene",
            description = null,
            tagsString = tagsString
        )

        // Assert
        assertThat(scene.tags).containsExactly("combat", "dungeon")
    }

    @Test
    fun `two scenes with same data are equal`() {
        // Arrange
        val scene1 = Scene(
            id = 1L,
            name = "Test",
            description = "Desc",
            tags = listOf("tag1", "tag2")
        )
        val scene2 = Scene(
            id = 1L,
            name = "Test",
            description = "Desc",
            tags = listOf("tag1", "tag2")
        )

        // Act & Assert
        assertThat(scene1).isEqualTo(scene2)
    }

    @Test
    fun `two scenes with different data are not equal`() {
        // Arrange
        val scene1 = Scene(id = 1L, name = "Scene 1")
        val scene2 = Scene(id = 2L, name = "Scene 2")

        // Act & Assert
        assertThat(scene1).isNotEqualTo(scene2)
    }
}
