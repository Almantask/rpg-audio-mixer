package com.example.rpgaudiomixer.infra.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RawResourceResolverTest {

    @Test
    fun functional_interface_can_be_implemented_with_lambda() {
        // Arrange & Act
        val resolver = RawResourceResolver { name ->
            when (name) {
                "test" -> 123
                else -> null
            }
        }

        // Assert
        assertThat(resolver.rawResIdOrNull("test")).isEqualTo(123)
        assertThat(resolver.rawResIdOrNull("other")).isNull()
    }

    @Test
    fun resolver_returning_null_for_non_existent_resource() {
        // Arrange
        val resolver = RawResourceResolver { null }

        // Act
        val result = resolver.rawResIdOrNull("any_name")

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun resolver_returning_valid_id_for_existing_resource() {
        // Arrange
        val expectedId = 456
        val resolver = RawResourceResolver { expectedId }

        // Act
        val result = resolver.rawResIdOrNull("resource_name")

        // Assert
        assertThat(result).isEqualTo(expectedId)
    }

    @Test
    fun resolver_can_differentiate_between_resources() {
        // Arrange
        val resourceMap = mapOf(
            "audio_thunder" to 100,
            "audio_rain" to 200,
            "audio_wind" to 300
        )
        val resolver = RawResourceResolver { name -> resourceMap[name] }

        // Act & Assert
        assertThat(resolver.rawResIdOrNull("audio_thunder")).isEqualTo(100)
        assertThat(resolver.rawResIdOrNull("audio_rain")).isEqualTo(200)
        assertThat(resolver.rawResIdOrNull("audio_wind")).isEqualTo(300)
        assertThat(resolver.rawResIdOrNull("audio_fire")).isNull()
    }

    @Test
    fun resolver_handles_empty_string() {
        // Arrange
        val resolver = RawResourceResolver { name ->
            if (name.isEmpty()) null else 999
        }

        // Act
        val result = resolver.rawResIdOrNull("")

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun multiple_resolvers_can_coexist() {
        // Arrange
        val resolver1 = RawResourceResolver { 100 }
        val resolver2 = RawResourceResolver { 200 }

        // Act
        val result1 = resolver1.rawResIdOrNull("test")
        val result2 = resolver2.rawResIdOrNull("test")

        // Assert
        assertThat(result1).isEqualTo(100)
        assertThat(result2).isEqualTo(200)
    }
}
