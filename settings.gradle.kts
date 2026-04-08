import org.gradle.kotlin.dsl.maven

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
    repositories {
        val googleMavenRepositoryUrl = providers
            .gradleProperty("googleMavenRepositoryUrl")
            .orElse(providers.environmentVariable("GOOGLE_MAVEN_REPOSITORY_URL"))
            .orNull
        if (googleMavenRepositoryUrl.isNullOrBlank()) {
            google {
                content {
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.google.*")
                    includeGroupByRegex("androidx.*")
                }
            }
        } else {
            maven(url = uri(googleMavenRepositoryUrl)) {
                name = "GoogleMirror"
                content {
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.google.*")
                    includeGroupByRegex("androidx.*")
                }
                metadataSources {
                    mavenPom()
                    artifact()
                }
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        val googleMavenRepositoryUrl = providers
            .gradleProperty("googleMavenRepositoryUrl")
            .orElse(providers.environmentVariable("GOOGLE_MAVEN_REPOSITORY_URL"))
            .orNull
        if (googleMavenRepositoryUrl.isNullOrBlank()) {
            google()
        } else {
            maven(url = uri(googleMavenRepositoryUrl)) {
                name = "GoogleMirror"
                content {
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.google.*")
                    includeGroupByRegex("androidx.*")
                }
                metadataSources {
                    mavenPom()
                    artifact()
                }
            }
        }
        mavenCentral()
    }
}

rootProject.name = "RPG Audio Mixer"
include(":app")
 
