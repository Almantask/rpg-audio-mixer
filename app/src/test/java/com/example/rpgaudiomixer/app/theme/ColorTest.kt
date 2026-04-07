package com.example.rpgaudiomixer.app.theme

import androidx.compose.ui.graphics.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColorTest {

    @Test
    fun `ArcanumGold has correct hex value`() {
        // Arrange
        val expectedColor = Color(0xFFF2CA50)

        // Act
        val actualColor = ArcanumGold

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `BackgroundBlack has correct hex value`() {
        // Arrange
        val expectedColor = Color(0xFF0A0A0A)

        // Act
        val actualColor = BackgroundBlack

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `ArcanumPurple has correct hex value`() {
        // Arrange
        val expectedColor = Color(0xFF9D4EDD)

        // Act
        val actualColor = ArcanumPurple

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `ErrorRed has correct hex value`() {
        // Arrange
        val expectedColor = Color(0xFFFFB4AB)

        // Act
        val actualColor = ErrorRed

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `TextPrimary is white`() {
        // Arrange
        val expectedColor = Color(0xFFFFFFFF)

        // Act
        val actualColor = TextPrimary

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `SurfaceDark has correct hex value`() {
        // Arrange
        val expectedColor = Color(0xFF1A1A1A)

        // Act
        val actualColor = SurfaceDark

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }

    @Test
    fun `CardSurface has correct hex value`() {
        // Arrange
        val expectedColor = Color(0xFF252525)

        // Act
        val actualColor = CardSurface

        // Assert
        assertThat(actualColor).isEqualTo(expectedColor)
    }
}
