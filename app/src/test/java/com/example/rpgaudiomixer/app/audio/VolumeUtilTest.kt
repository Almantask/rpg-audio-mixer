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

    @Test
    fun equalPowerFadeIn_at_zero_returns_zero() {
        // Act
        val result = VolumeUtil.equalPowerFadeIn(0f)
        // Assert
        assertThat(result).isCloseTo(0f, Offset.offset(0.001f))
    }

    @Test
    fun equalPowerFadeIn_at_one_returns_one() {
        val result = VolumeUtil.equalPowerFadeIn(1f)
        assertThat(result).isCloseTo(1f, Offset.offset(0.001f))
    }

    @Test
    fun equalPowerFadeOut_at_zero_returns_one() {
        val result = VolumeUtil.equalPowerFadeOut(0f)
        assertThat(result).isCloseTo(1f, Offset.offset(0.001f))
    }

    @Test
    fun equalPowerFadeOut_at_one_returns_zero() {
        val result = VolumeUtil.equalPowerFadeOut(1f)
        assertThat(result).isCloseTo(0f, Offset.offset(0.001f))
    }

    @Test
    fun equalPower_crossfade_preserves_constant_power() {
        // At midpoint, fadeIn² + fadeOut² ≈ 1
        val mid = 0.5f
        val fadeIn = VolumeUtil.equalPowerFadeIn(mid)
        val fadeOut = VolumeUtil.equalPowerFadeOut(mid)
        assertThat(fadeIn * fadeIn + fadeOut * fadeOut).isCloseTo(1f, Offset.offset(0.001f))
    }
}
