package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.ErrorRed
import com.example.rpgaudiomixer.app.theme.Gold
import com.example.rpgaudiomixer.app.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit
) {
    if (message != null) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clip(Shapes.large)
                .background(BlackBg)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "A DARK CURSE BEFALLS US",
                    color = ErrorRed,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = message,
                        color = Gold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed,
                        contentColor = BlackBg
                    ),
                    shape = Shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "DISPEL")
                }
            }
        }
    }
}
