package com.example.rpgaudiomixer.app.navigation

/**
 * Main navigation destinations for Arcanum Audio
 *
 * @param route Navigation route identifier
 * @param label Display label for navigation bar
 */
enum class MainNavDestination(
    val route: String,
    val label: String
) {
    HOME("home", "Home"),
    CAMPAIGNS("campaigns", "Campaigns"),
    SCENES("scenes", "Scenes"),
    LIBRARY("library", "Library")
}
