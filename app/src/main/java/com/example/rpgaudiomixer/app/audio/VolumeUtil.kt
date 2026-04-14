package com.example.rpgaudiomixer.app.audio

/**
 * Utility for converting linear slider positions to perceptual volume using a cubic gain curve.
 *
 * A cubic curve (`x³`) maps a 0–1 slider value to a 0–1 gain value that
 * better matches human loudness perception than a linear mapping.
 */
object VolumeUtil {

    /**
     * Applies a cubic gain curve to a linear slider value.
     *
     * @param sliderValue linear position in 0.0–1.0
     * @return perceptual gain in 0.0–1.0
     */
    fun cubicVolume(sliderValue: Float): Float =
        sliderValue * sliderValue * sliderValue
}
