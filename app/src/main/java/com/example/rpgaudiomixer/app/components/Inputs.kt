package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, color = ArcanumOnSurface.copy(alpha = 0.4f)) },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ArcanumGold,
            unfocusedBorderColor = ArcanumOnSurface.copy(alpha = 0.1f),
            focusedContainerColor = ArcanumCard,
            unfocusedContainerColor = ArcanumCard,
            cursorColor = ArcanumGold
        ),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = ArcanumMutedGold)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = ArcanumMutedGold)
                }
            }
        },
        singleLine = true
    )
}
