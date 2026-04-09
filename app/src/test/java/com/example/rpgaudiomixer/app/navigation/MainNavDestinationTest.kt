package com.example.rpgaudiomixer.app.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainNavDestinationTest {

    @Test
    fun destinations_have_correct_labels() {
        assertEquals("HOME", MainNavDestination.HOME.label)
        assertEquals("CAMPAIGNS", MainNavDestination.CAMPAIGNS.label)
        assertEquals("SCENES", MainNavDestination.SCENES.label)
        assertEquals("LIBRARY", MainNavDestination.LIBRARY.label)
    }
}
