package com.example.rpgaudiomixer.app.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.example.rpgaudiomixer.ui.home.HomeScreen as HomeScreenImpl
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen as CampaignsScreenImpl

import com.example.rpgaudiomixer.ui.home.HomeViewModel
import com.example.rpgaudiomixer.ui.campaigns.CampaignsViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    HomeScreenImpl(modifier = modifier, viewModel = viewModel)
}

@Composable
fun CampaignsScreen(modifier: Modifier = Modifier, viewModel: CampaignsViewModel) {
    CampaignsScreenImpl(modifier = modifier, viewModel = viewModel)
}

@Composable
fun ScenesScreen(modifier: Modifier = Modifier) {
    Text("Scenes Screen", modifier = modifier)
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Text("Library Screen", modifier = modifier)
}
