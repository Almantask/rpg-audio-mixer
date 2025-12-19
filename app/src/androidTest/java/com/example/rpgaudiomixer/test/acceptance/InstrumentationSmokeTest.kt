package com.example.rpgaudiomixer.test.acceptance

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Test

class InstrumentationSmokeTest {

    @Test
    fun instrumentation_is_available() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        assertNotNull(instrumentation)
        assertNotNull(instrumentation.targetContext)
    }
}
