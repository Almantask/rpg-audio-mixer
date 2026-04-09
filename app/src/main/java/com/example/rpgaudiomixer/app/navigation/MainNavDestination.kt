package com.example.rpgaudiomixer.app.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Fort

enum class MainNavDestination(
    val label: String,
    val icon: ImageVector
) {
    HOME("HOME", Icons.Default.Fort),
    CAMPAIGNS("CAMPAIGNS", Icons.Default.AutoStories),
    SCENES("SCENES", Icons.Default.Image),
    LIBRARY("LIBRARY", Icons.Default.MusicNote)
}
