package com.example.rpgaudiomixer.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Arcanum Audio Shape System
 *
 * Rounded corners for cards, buttons, and other UI elements.
 */
val ArcanumShapes = Shapes(
    // Small - Chips, small buttons
    small = RoundedCornerShape(8.dp),

    // Medium - Cards, buttons
    medium = RoundedCornerShape(12.dp),

    // Large - Dialogs, bottom sheets
    large = RoundedCornerShape(16.dp)
)
