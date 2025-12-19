package com.example.rpgaudiomixer.test

import io.cucumber.junit.CucumberOptions

/**
 * Cucumber's Android integration requires exactly one class annotated with [CucumberOptions]
 * in the *androidTest APK package* so the runner can discover features and glue.
 *
 * Note: This is not a JUnit test class and should not have @RunWith.
 */
@CucumberOptions(
    features = ["features"],
    glue = ["com.example.rpgaudiomixer.acceptance.steps"],
    plugin = ["pretty"]
)
class CucumberTestConfig
