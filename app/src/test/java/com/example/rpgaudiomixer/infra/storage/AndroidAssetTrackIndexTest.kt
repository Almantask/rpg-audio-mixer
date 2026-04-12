package com.example.rpgaudiomixer.infra.storage

import android.content.res.AssetManager
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class AndroidAssetTrackIndexTest {

    private val mockAssetManager: AssetManager = mockk()
    private val sut = AndroidAssetTrackIndex(mockAssetManager)

    @Test
    fun `exists returns true when assetManager open succeeds`() {
        // Arrange
        val assetPath = "audio/battle.mp3"
        every { mockAssetManager.open(assetPath) } returns ByteArrayInputStream(byteArrayOf())

        // Act
        val result = sut.exists(assetPath)

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun `exists returns false when assetManager open throws`() {
        // Arrange
        val assetPath = "audio/missing.mp3"
        every { mockAssetManager.open(assetPath) } throws java.io.FileNotFoundException("not found")

        // Act
        val result = sut.exists(assetPath)

        // Assert
        assertThat(result).isFalse()
    }
}
