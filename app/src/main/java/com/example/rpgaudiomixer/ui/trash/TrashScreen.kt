package com.example.rpgaudiomixer.ui.trash

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.components.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vault of Echoes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Empty state - soft delete not yet implemented
            EmptyStateView(
                title = "Vault is Empty",
                message = "Deleted items will appear here for 7 days before permanent removal",
                actionLabel = "BACK",
                onAction = onNavigateBack
            )

            // Footer text
            Text(
                text = "Items will be permanently removed 7 days after deletion",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
