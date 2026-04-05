package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.app.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionCard(
    session: Session,
    onClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        onClick = { onClick(session.id) }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Cover Art Section
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            ) {
                if (session.coverArtUri != null) {
                    AsyncImage(
                        model = session.coverArtUri,
                        contentDescription = "Session Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = session.name.uppercase(),
                    style = Typography.titleLarge,
                    color = Gold,
                    maxLines = 1
                )
                
                Text(
                    text = formatDate(session.date),
                    style = Typography.labelLarge,
                    color = Gold.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
