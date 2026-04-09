package com.example.rpgaudiomixer.app.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape

fun Modifier.glowBorder(
    isPlaying: Boolean,
    color: Color,
): Modifier {
    val shape = RoundedCornerShape(16.dp)
    return if (isPlaying) {
        this
            .clip(shape)
            .border(width = 2.dp, color = color, shape = shape)
    } else {
        this.clip(shape)
    }
}
