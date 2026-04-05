package com.example.rpgaudiomixer.app.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.*

enum class LibraryTab(val title: String) {
    SOUNDSCAPES("SOUNDSCAPES"),
    SOUND_EFFECTS("SOUND EFFECTS")
}

@Composable
fun LibraryScreen(
    onNavigateToSoundscapeComposer: (Long) -> Unit
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.SOUNDSCAPES) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "ARCHIVE OF ECHOES",
                showBackArrow = false,
                onBack = {}
            )
        },
        containerColor = BlackBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Strip
            LibraryTabStrip(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    LibraryTab.SOUNDSCAPES -> {
                        SoundscapeLibraryScreen(
                            onEditCategory = onNavigateToSoundscapeComposer
                        )
                    }
                    LibraryTab.SOUND_EFFECTS -> {
                        FXLibraryScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryTabStrip(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 16.dp)
            .background(CardSurface, shape = RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LibraryTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            Surface(
                onClick = { onTabSelected(tab) },
                color = if (isSelected) Gold else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) BlackBg else Gold.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        style = Typography.labelLarge
                    )
                }
            }
        }
    }
}
