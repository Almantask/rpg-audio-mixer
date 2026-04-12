package com.example.rpgaudiomixer.infra.storage

import android.content.Context
import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AndroidRawResourceResolverTest {

    private val mockResources: Resources = mockk()
    private val mockContext: Context = mockk {
        every { resources } returns mockResources
        every { packageName } returns "com.example.rpgaudiomixer"
    }
    private val sut = AndroidRawResourceResolver(mockContext)

    @Test
    fun `rawResIdOrNull returns id when resource exists`() {
        // Arrange
        every { mockResources.getIdentifier("dog_bark", "raw", "com.example.rpgaudiomixer") } returns 12345

        // Act
        val result = sut.rawResIdOrNull("dog_bark")

        // Assert
        assertThat(result).isEqualTo(12345)
    }

    @Test
    fun `rawResIdOrNull returns null when resource does not exist`() {
        // Arrange
        every { mockResources.getIdentifier("unknown_track", "raw", "com.example.rpgaudiomixer") } returns 0

        // Act
        val result = sut.rawResIdOrNull("unknown_track")

        // Assert
        assertThat(result).isNull()
    }
}
