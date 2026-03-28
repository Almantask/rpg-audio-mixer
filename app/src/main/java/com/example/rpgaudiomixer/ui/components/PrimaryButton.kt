package com.example.rpgaudiomixer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumGold

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = ArcanumGold,
            contentColor = ArcanumBlack,
        ),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
