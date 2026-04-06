package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceDim
import com.example.rpgaudiomixer.ui.library.fx.FxLibraryTab
import com.example.rpgaudiomixer.ui.library.soundscapes.SoundscapesLibraryTab

@Composable
fun LibraryScreen(
    onGearClick: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Library",
                onGearClick = onGearClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ArcanumGold,
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Soundscapes",
                            color = if (selectedTab == 0) ArcanumGold else ArcanumOnSurfaceDim,
                        )
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Sound Effects",
                            color = if (selectedTab == 1) ArcanumGold else ArcanumOnSurfaceDim,
                        )
                    },
                )
            }
            when (selectedTab) {
                0 -> SoundscapesLibraryTab()
                1 -> FxLibraryTab()
            }
        }
    }
}
