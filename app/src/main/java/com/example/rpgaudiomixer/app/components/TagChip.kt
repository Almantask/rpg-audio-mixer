package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun TagChip(tag: String) {
    Surface(
        color = Gold.copy(alpha = 0.1f),
        shape = Shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
    ) {
        Text(
            text = tag.uppercase(),
            color = Gold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
