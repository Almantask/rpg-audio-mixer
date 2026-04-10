package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.Session
import java.text.DateFormat
import java.util.Date

@Composable
fun SessionCard(
    session: Session,
    onOpen: (Session) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpen(session) },
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = session.coverArtUri,
                contentDescription = "${session.name} cover art",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "${session.formattedDate()} • ${session.sceneCount} scenes",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

private fun Session.formattedDate(): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(dateMillis))
}
