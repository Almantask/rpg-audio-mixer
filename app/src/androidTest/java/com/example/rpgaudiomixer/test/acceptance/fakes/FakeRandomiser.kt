package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.media.Randomiser

/**
 * Pure fake implementation with no external dependencies.
 *
 * PicoContainer constructs this automatically and injects it into step definitions and rules.
 */
class FakeRandomiser : Randomiser {
    var nextValue: Int = 0

    override fun nextInt(until: Int): Int = nextValue
}
