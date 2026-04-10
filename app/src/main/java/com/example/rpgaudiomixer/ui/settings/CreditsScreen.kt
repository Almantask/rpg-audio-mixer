package com.example.rpgaudiomixer.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.BuildConfig
import com.example.rpgaudiomixer.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class CreditsUiState(
    val isSyncEnabled: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun CreditsRoute(
    onOpenTrash: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreditsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CreditsScreen(
        uiState = uiState,
        onSyncPurchasesAndFreeTracks = viewModel::syncPurchasesAndFreeTracks,
        onOpenTrash = onOpenTrash,
        modifier = modifier,
    )
}

@Composable
fun CreditsScreen(
    uiState: CreditsUiState,
    onSyncPurchasesAndFreeTracks: () -> Unit,
    onOpenTrash: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Arcanum Audio",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Version ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSyncPurchasesAndFreeTracks,
            enabled = uiState.isSyncEnabled && !uiState.isSyncing,
        ) {
            Text(text = "Sync Purchases & Free Tracks")
        }
        Text(
            text = "Available once per day",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenTrash,
        ) {
            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Restore Recent Deletes",
            )
        }

        SectionCard(
            title = "Credits",
            lines = listOf(
                "Design & Development — Arcanum Audio",
                "Made with ❤️ for GMs everywhere",
            ),
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Links",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                LinkRow(
                    icon = Icons.Default.Description,
                    label = "Documentation",
                    onClick = { uriHandler.openUri(DOCUMENTATION_URL) },
                )
                LinkRow(
                    icon = Icons.Default.Forum,
                    label = "Discord community",
                    onClick = { uriHandler.openUri(DISCORD_URL) },
                )
                LinkRow(
                    icon = Icons.Default.Email,
                    label = "Contact / support email",
                    onClick = { uriHandler.openUri(SUPPORT_EMAIL_URI) },
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    lines: List<String>,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            lines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun LinkRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = label,
        )
    }
}

@HiltViewModel
class CreditsViewModel(
    private val settingsRepository: SettingsRepository,
    private val currentTimeProvider: () -> Long,
    private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {
    @Inject
    constructor(
        settingsRepository: SettingsRepository,
    ) : this(
        settingsRepository = settingsRepository,
        currentTimeProvider = System::currentTimeMillis,
        mainDispatcher = Dispatchers.Main,
    )

    private val _uiState = MutableStateFlow(CreditsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            settingsRepository.observeLastSuccessfulSyncAt()
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = throwable.message ?: "Unable to load settings.",
                    )
                }
                .collect { lastSuccessfulSyncAt ->
                    _uiState.value = _uiState.value.copy(
                        isSyncEnabled = lastSuccessfulSyncAt == null ||
                            currentTimeProvider() - lastSuccessfulSyncAt >= ONE_DAY_IN_MILLIS,
                        isSyncing = false,
                    )
                }
        }
    }

    fun syncPurchasesAndFreeTracks() {
        if (!_uiState.value.isSyncEnabled || _uiState.value.isSyncing) {
            return
        }
        viewModelScope.launch(mainDispatcher) {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            settingsRepository.syncPurchasesAndFreeTracks()
        }
    }

    private companion object {
        const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}

private const val DOCUMENTATION_URL = "https://github.com/Almantask/rpg-audio-mixer"
private const val DISCORD_URL = "https://discord.com"
private const val SUPPORT_EMAIL_URI = "mailto:support@arcanum-audio.example"
