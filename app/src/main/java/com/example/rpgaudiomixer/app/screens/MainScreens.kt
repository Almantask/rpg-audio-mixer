package com.example.rpgaudiomixer.app.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rpgaudiomixer.app.ui.ActiveSceneViewModel
import com.example.rpgaudiomixer.app.ui.CampaignsViewModel
import com.example.rpgaudiomixer.app.ui.HomeViewModel
import com.example.rpgaudiomixer.app.ui.LibraryViewModel
import com.example.rpgaudiomixer.app.ui.ScenesViewModel
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    canNavigateBack: Boolean = false,
    onGearClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            IconButton(onClick = onGearClick) {
                Icon(Icons.Default.Settings, contentDescription = "Credits")
            }
        }
    )
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTopBar(title = "Home", navController = navController, canNavigateBack = false) {
            navController.navigate("credits")
        }

        if (state.activeCampaign != null) {
            CampaignCard(state.activeCampaign, onEnterCampaign = {
                viewModel.markCampaignPlayed(it.id)
                navController.navigate("campaign/${it.id}")
            }, title = "Active Campaign")
        } else {
            Text("No active campaign yet. Start by adding one in Campaigns.")
        }

        state.resumeScene?.let { scene ->
            Card(modifier = Modifier.fillMaxWidth().clickable {
                viewModel.markScenePlayed(scene.id)
                navController.navigate("scene/${scene.id}")
            }) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Resume Journey", style = MaterialTheme.typography.titleMedium)
                    Text(scene.name)
                    Text("Tap ENTER to continue")
                    OutlinedButton(onClick = {
                        viewModel.markScenePlayed(scene.id)
                        navController.navigate("scene/${scene.id}")
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Enter")
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("ENTER")
                    }
                }
            }
        } ?: Text("No scene to resume yet.")

        Divider()

        Text("Top Atmosphere", style = MaterialTheme.typography.titleMedium)
        Text(state.topAtmosphere?.name ?: "None yet")

        Text("Legendary Action", style = MaterialTheme.typography.titleMedium)
        Text(state.legendaryAction?.name ?: "None yet")
    }
}

@Composable
fun CampaignsScreen(navController: NavController, viewModel: CampaignsViewModel = hiltViewModel()) {
    val contexts = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTopBar(title = "Campaigns", navController = navController, canNavigateBack = false) {
            navController.navigate("credits")
        }

        val campaigns = viewModel.getCampaigns()
        if (campaigns.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("No tales scribed yet.")
                Button(onClick = { viewModel.addSampleCampaign() }) {
                    Text("Scribe New Tale")
                }
            }
            return@Column
        }

        campaigns.forEach { campaign ->
            CampaignCard(campaign, onEnterCampaign = {
                viewModel.resumeCampaign(campaign.id)
                navController.navigate("campaign/${campaign.id}")
            }, title = "${campaign.name}")
        }
    }
}

@Composable
private fun CampaignCard(campaign: Campaign, onEnterCampaign: (Campaign) -> Unit, title: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(campaign.name, style = MaterialTheme.typography.titleSmall)
            Text(campaign.description ?: "No description")
            Button(onClick = { onEnterCampaign(campaign) }) {
                Text("RESUME")
            }
        }
    }
}

@Composable
fun CampaignSessionsScreen(navController: NavController, campaignId: String, viewModel: CampaignsViewModel = hiltViewModel()) {
    val campaign = viewModel.getCampaigns().firstOrNull { it.id == campaignId }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTopBar(title = campaign?.name ?: "Sessions", navController = navController, canNavigateBack = true) {
            navController.navigate("credits")
        }

        if (campaign == null) {
            Text("Campaign not found")
            return@Column
        }

        val sessions = viewModel.getSessionsForCampaign(campaignId)
        if (sessions.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("No sessions yet")
                Button(onClick = {
                    val session = com.example.rpgaudiomixer.domain.model.Session(campaignId = campaignId, name = "Session 1")
                    viewModel.addSession(session)
                }) {
                    Text("ADD NEW SESSION")
                }
            }
            return@Column
        }
    }
}

@Composable
fun ScenesScreen(navController: NavController, viewModel: ScenesViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppTopBar(title = "Scenes", navController = navController, canNavigateBack = false) {
            navController.navigate("credits")
        }

        val scenes = viewModel.getScenes()
        if (scenes.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("No scenes yet")
                Button(onClick = { viewModel.addSampleScene() }) {
                    Text("Add New Scene")
                }
            }
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scenes) { scene ->
                SceneCard(scene,
                    onOpen = { navController.navigate("scene/${scene.id}") },
                    onPlay = {
                        viewModel.addTagToScene(scene.id, "played")
                        navController.navigate("scene/${scene.id}")
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.addSampleScene() }) {
                    Text("Add New Scene")
                }
            }
        }
    }
}

@Composable
private fun SceneCard(scene: Scene, onOpen: () -> Unit, onPlay: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(scene.name, style = MaterialTheme.typography.titleMedium)
            Text(scene.tags.joinToString(", ").ifEmpty { "No tags" })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpen) { Text("Open") }
                Button(onClick = onPlay) { Icon(Icons.Default.PlayArrow, contentDescription = "Play") }
            }
        }
    }
}

@Composable
fun ActiveSceneScreen(navController: NavController, sceneId: String, viewModel: ActiveSceneViewModel = hiltViewModel()) {
    val state = viewModel.getDefaultState(sceneId)
    var selectedTab by remember { mutableStateOf(0) }
    var masterVolume by remember { mutableStateOf(1f) }
    var soundboardMaster by remember { mutableStateOf(1f) }
    var playingState by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = state.scene?.name ?: "Active Scene", navController = navController, canNavigateBack = true) {
            navController.navigate("credits")
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Soundscapes", "Soundboard").forEachIndexed { index, title ->
                Button(onClick = { selectedTab = index }, modifier = Modifier.weight(1f)) {
                    Text(title)
                }
            }
        }

        when (selectedTab) {
            0 -> {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Master Atmosphere", style = MaterialTheme.typography.titleMedium)
                    Slider(value = masterVolume, onValueChange = { masterVolume = it }, valueRange = 0f..1f)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    state.scene?.soundscapeCategoryIds?.forEach { categoryId ->
                        val cat = viewModel.getAllSoundscapeCategories().firstOrNull { it.id == categoryId }
                        cat?.let {
                            Text(it.name)
                            Slider(value = 1f, onValueChange = {}, valueRange = 0f..1f)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* random track */ }) {
                            Icon(Icons.Default.Casino, contentDescription = "d20")
                        }
                        Button(onClick = { playingState = !playingState }) {
                            if (playingState) Text("Pause") else Text("Play")
                        }
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Soundboard", style = MaterialTheme.typography.titleMedium)
                    Slider(value = soundboardMaster, onValueChange = { soundboardMaster = it }, valueRange = 0f..1f)
                    val effects = viewModel.getAllSoundEffects()
                    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(300.dp)) {
                        gridItems(effects) { effect ->
                            Button(onClick = { /* trigger effect */ }, modifier = Modifier.padding(4.dp)) {
                                Text(effect.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(navController: NavController, viewModel: LibraryViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AppTopBar(title = "Library", navController = navController, canNavigateBack = false) {
            navController.navigate("credits")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { selectedTab = 0 }) { Text("Soundscapes") }
            Button(onClick = { selectedTab = 1 }) { Text("FX") }
        }

        if (selectedTab == 0) {
            val categories = viewModel.getCategories()
            if (categories.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("No soundscape categories yet")
                    Button(onClick = { viewModel.addSampleCategory() }) { Text("Create first category") }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(category.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Intensity ${category.intensityLevel} | ${category.layerTrackIds.size} layers")
                                }
                                IconButton(onClick = { navController.navigate("soundscapeComposer/${category.id}") }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val effects = viewModel.getEffects()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.addSampleEffect() }) { Text("Import FX") }
            }
            if (effects.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("No FX in the library")
                    Button(onClick = { viewModel.addSampleEffect() }) { Text("Import FX") }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(effects) { effect ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(effect.name)
                                IconButton(onClick = { /* navigate to effect edit screen */}) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
            MiniPlayerBar()
        }
    }
}

@Composable
fun MiniPlayerBar() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.DarkGray)
        .padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mini player", color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* play/pause */ }) { Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White) }
                IconButton(onClick = { /* stop */ }) { Icon(Icons.Default.Shuffle, contentDescription = "Stop", tint = Color.White) }
            }
        }
    }
}

@Composable
fun SoundscapeComposerScreen(navController: NavController, categoryId: String?, viewModel: LibraryViewModel = hiltViewModel()) {
    val category = viewModel.getCategories().firstOrNull { it.id == categoryId }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AppTopBar(title = "Soundscape Composer", navController = navController, canNavigateBack = true) {
            navController.navigate("credits")
        }
        if (category == null) {
            Text("Category not found")
            return@Column
        }
        Text("${category.name} (${category.intensityLevel})")
        Text("Layers:")
        category.layerTrackIds.forEach { trackId ->
            Text("• $trackId")
        }
        Button(onClick = {
            val updated = category.copy(layerTrackIds = category.layerTrackIds + "new_track_${System.currentTimeMillis()}")
            viewModel.updateCategory(updated)
        }) {
            Text("Add layer (file picker stub)")
        }
        Button(onClick = { navController.navigateUp() }) { Text("Save Composition") }
    }
}

@Composable
fun CreditsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AppTopBar(title = "Credits", navController = navController, canNavigateBack = true) {
            navController.navigateUp()
        }
        Text("Arcanum Audio")
        Text("Version: 1.0")
        Text("Support: https://example.com/support")
        Text("Docs: https://example.com/docs")
        Text("Discord: https://discord.gg/example")
        Text("Email: support@example.com")
    }
}

@Composable
fun SceneComposerScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AppTopBar(title = "Add New Scene", navController = navController, canNavigateBack = true) {
            navController.navigateUp()
        }
        Text("Scene composer stub")
        Button(onClick = { navController.navigateUp() }) {
            Text("Close")
        }
    }
}
