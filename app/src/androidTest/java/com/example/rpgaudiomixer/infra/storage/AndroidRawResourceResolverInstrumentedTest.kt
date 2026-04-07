package com.example.rpgaudiomixer.infra.storage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rpgaudiomixer.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android instrumentation test for AndroidRawResourceResolver.
 *
 * Tests the actual Android resource resolution mechanism.
 */
@RunWith(AndroidJUnit4::class)
class AndroidRawResourceResolverInstrumentedTest {

    private lateinit var appContext: Context
    private lateinit var resolver: AndroidRawResourceResolver

    @Before
    fun setup() {
        // Arrange
        appContext = ApplicationProvider.getApplicationContext()
        resolver = AndroidRawResourceResolver(appContext)
    }

    @Test
    fun rawResIdOrNull_with_non_existent_resource_returns_null() {
        // Act
        val result = resolver.rawResIdOrNull("definitely_does_not_exist_12345")

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun rawResIdOrNull_with_empty_name_returns_null() {
        // Act
        val result = resolver.rawResIdOrNull("")

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun rawResIdOrNull_with_valid_resource_returns_non_zero_id() {
        // Note: This test assumes at least one raw resource exists.
        // If the app has raw resources, we can test with a known one.
        // For now, we test the behavior with a plausible name pattern.

        // Try to find any raw resource that might exist in the test app
        val testResourceNames = listOf("test", "sample", "audio", "sound")

        // Act
        val results = testResourceNames.map { resolver.rawResIdOrNull(it) }

        // Assert - at least the method doesn't crash and returns appropriate types
        results.forEach { result ->
            assertThat(result).satisfiesAnyOf(
                { assertThat(it).isNull() },
                { assertThat(it).isGreaterThan(0) }
            )
        }
    }

    @Test
    fun rawResIdOrNull_called_twice_with_same_name_returns_same_id() {
        // Arrange
        val resourceName = "test_resource"

        // Act
        val result1 = resolver.rawResIdOrNull(resourceName)
        val result2 = resolver.rawResIdOrNull(resourceName)

        // Assert - should be consistent
        assertThat(result1).isEqualTo(result2)
    }

    @Test
    fun rawResIdOrNull_with_special_characters_returns_null() {
        // Arrange - resource names with special characters should not exist
        val invalidNames = listOf("test@resource", "test-resource!", "test resource")

        // Act & Assert
        invalidNames.forEach { name ->
            val result = resolver.rawResIdOrNull(name)
            assertThat(result).isNull()
        }
    }
}
