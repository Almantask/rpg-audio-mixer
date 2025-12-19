package com.example.rpgaudiomixer.test

/**
 * This class is discovered by `io.cucumber.android.CucumberJunitRunner` via classpath scanning.
 *
 * Notes:
 * - This is used for Android *instrumented* Cucumber runs (not local JVM tests).
 * - Options can also be overridden/extended via `testInstrumentationRunnerArguments`.
 */
@io.cucumber.junit.CucumberOptions(
	features = ["classpath:features"],
	glue = ["com.example.rpgaudiomixer.test.acceptance"],
	plugin = ["pretty"],
)
class CucumberOptionsConfig
