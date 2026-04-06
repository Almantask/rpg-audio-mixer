package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.domain.model.Scene
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneMapperTest {

    @Test
    fun `toDomain maps entity fields correctly`() {
        // Arrange
        val entity = SceneEntity(
            id = 1L,
            name = "Tavern Brawl",
            description = "A rowdy tavern fight",
            tags = "Tavern,Combat,Indoor",
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertThat(domain.id).isEqualTo(1L)
        assertThat(domain.name).isEqualTo("Tavern Brawl")
        assertThat(domain.description).isEqualTo("A rowdy tavern fight")
        assertThat(domain.tags).containsExactly("Tavern", "Combat", "Indoor")
    }

    @Test
    fun `toDomain maps empty tags correctly`() {
        // Arrange
        val entity = SceneEntity(id = 2L, name = "Empty Scene", tags = "")

        // Act
        val domain = entity.toDomain()

        // Assert
        assertThat(domain.tags).isEmpty()
    }

    @Test
    fun `toEntity maps domain tags to comma-separated string`() {
        // Arrange
        val scene = Scene(id = 3L, name = "Forest Path", tags = listOf("Forest", "Outdoors"))

        // Act
        val entity = scene.toEntity()

        // Assert
        assertThat(entity.tags).isEqualTo("Forest,Outdoors")
    }

    @Test
    fun `toEntity maps empty tags list to empty string`() {
        // Arrange
        val scene = Scene(id = 4L, name = "Blank Scene", tags = emptyList())

        // Act
        val entity = scene.toEntity()

        // Assert
        assertThat(entity.tags).isEmpty()
    }
}
