package com.example.rpgaudiomixer.app.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumGold

@Composable
fun Modifier.glowBorder(isActive: Boolean): Modifier {
    val borderColor = animateColorAsState(
        targetValue = if (isActive) ArcanumGold else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(durationMillis = 250),
        label = "glowBorderColor",
    )

    return this
        .clip(RoundedCornerShape(20.dp))
        .border(
            width = if (isActive) 2.dp else 1.dp,
            color = borderColor.value,
            shape = RoundedCornerShape(20.dp),
        )
        .padding(1.dp)
}
