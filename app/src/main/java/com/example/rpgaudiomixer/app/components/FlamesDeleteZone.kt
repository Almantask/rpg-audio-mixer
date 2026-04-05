package com.example.rpgaudiomixer.app.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun FlamesDeleteZone(
    isVisible: Boolean,
    isHovered: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flames")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isHovered) Color.Red.copy(alpha = 0.5f) else Gold.copy(alpha = 0.2f),
                            if (isHovered) Color.Red else Gold.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (isHovered) Color.White else Color.Red.copy(alpha = glowAlpha),
                    modifier = Modifier.size(if (isHovered) 40.dp else 32.dp)
                )
                Text(
                    text = if (isHovered) "RELEASE TO CONSUME" else "DRAG TO FLAMES",
                    style = Typography.labelLarge,
                    color = if (isHovered) Color.White else Color.Red.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
