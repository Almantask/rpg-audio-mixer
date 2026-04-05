package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.CardSurface
import com.example.rpgaudiomixer.app.theme.Gold
import com.example.rpgaudiomixer.app.theme.Shapes
import com.example.rpgaudiomixer.app.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CampaignCard(
    campaign: Campaign,
    onResume: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Cover Art Section
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            ) {
                if (campaign.coverArtUri != null) {
                    AsyncImage(
                        model = campaign.coverArtUri,
                        contentDescription = "Campaign Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                // Gradient overlay for consistent look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, CardSurface.copy(alpha = 0.5f)),
                                startX = 0f,
                                endX = 300f
                            )
                        )
                )
            }

            // Info Section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = campaign.name.uppercase(),
                    style = Typography.titleLarge,
                    color = Gold,
                    maxLines = 1
                )
                
                Text(
                    text = "LAST PLAYED: ${formatDate(campaign.lastPlayedAt)}",
                    style = Typography.labelLarge,
                    color = Gold.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onResume(campaign.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = BlackBg
                    ),
                    shape = Shapes.small,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "RESUME",
                        style = Typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
