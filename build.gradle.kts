// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.sonarqube)
}

sonar {
    properties {
        property("sonar.projectKey", "Almantask_rpg-audio-mixer")
        property("sonar.organization", "almantask")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.qualitygate.wait", "true")
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        config.setFrom(files("${project.rootDir}/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        baseline = file("detekt-baseline.xml")
    }
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(false)
        }
    }
}