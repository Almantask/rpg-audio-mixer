package com.example.rpgaudiomixer.app.audio

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class VolumeUtilTest {

    @ParameterizedTest
    @CsvSource(
        "0.0, 0.0",
        "1.0, 1.0",
        "0.5, 0.125",
    )
    fun `cubicVolume returns the cube of the slider value`(input: Float, expected: Float) {
        // Arrange — parameters provided

        // Act
        val result = VolumeUtil.cubicVolume(input)

        // Assert
        assertThat(result).isCloseTo(expected, Offset.offset(0.0001f))
    }

    @Test
    fun `cubicVolume at quarter produces one sixty-fourth`() {
        // Arrange
        val input = 0.25f

        // Act
        val result = VolumeUtil.cubicVolume(input)

        // Assert
        assertThat(result).isCloseTo(0.015625f, Offset.offset(0.00001f))
    }

    @Test
    fun `cubicVolume at zero returns zero`() {
        // Arrange
        val input = 0.0f

        // Act
        val result = VolumeUtil.cubicVolume(input)

        // Assert
        assertThat(result).isEqualTo(0.0f)
    }

    @Test
    fun `cubicVolume at one returns one`() {
        // Arrange
        val input = 1.0f

        // Act
        val result = VolumeUtil.cubicVolume(input)

        // Assert
        assertThat(result).isEqualTo(1.0f)
    }

    @Test
    fun `cubicVolume preserves monotonicity for increasing values`() {
        // Arrange
        val low = 0.3f
        val high = 0.7f

        // Act
        val resultLow = VolumeUtil.cubicVolume(low)
        val resultHigh = VolumeUtil.cubicVolume(high)

        // Assert
        assertThat(resultLow).isLessThan(resultHigh)
    }
}
