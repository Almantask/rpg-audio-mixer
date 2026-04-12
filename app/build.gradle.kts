plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.example.rpgaudiomixer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rpgaudiomixer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        //testInstrumentationRunner = "com.example.rpgaudiomixer.acceptance.CucumberJunitRunner"
        // Prefer our custom runner so we can keep report outputs stable.
        testInstrumentationRunner = "com.example.rpgaudiomixer.test.acceptance.CucumberJunitRunner"
        testApplicationId = "com.example.rpgaudiomixer.test"

        // Configure Cucumber for Android instrumentation runs.
        // This replaces deprecated/incorrect usage of io.cucumber.junit.CucumberOptions in androidTest.
        val cucumberFeatures = project.findProperty("cucumberFeatures") as? String ?: "features"
        testInstrumentationRunnerArguments["cucumberFeatures"] = cucumberFeatures
        
        val cucumberTags = project.findProperty("cucumberTags") as? String
        if (!cucumberTags.isNullOrBlank()) {
            testInstrumentationRunnerArguments["tags"] = cucumberTags
        }

        testInstrumentationRunnerArguments["cucumberGlue"] = "com.example.rpgaudiomixer.test.acceptance"
        // Keep at least one human-readable plugin; runner adds junit/html reports.
        testInstrumentationRunnerArguments["plugin"] = "pretty"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    dependencies {
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.material.icons.extended)
        implementation(libs.androidx.navigation.compose)
    }

    testOptions {
        animationsDisabled = true

        unitTests.all {
            it.useJUnitPlatform()
        }

//         managedDevices {
//             devices {
//                 maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel4Api33").apply {
//                     device = "Pixel 4"
//                     apiLevel = 33
//                     systemImageSource = "aosp"
//                 }
//             }
//         }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Audio playback
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.common)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Local JVM unit tests (JUnit 5)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockk.core)
    testRuntimeOnly(libs.junit.platform.launcher)
    androidTestImplementation(libs.cucumber.android)
        androidTestImplementation(libs.cucumber.junit)
    androidTestImplementation(libs.cucumber.picocontainer)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Hilt testing (instrumentation)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}