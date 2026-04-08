package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.BentoCard
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SoundscapeLibraryUiState(
    val isLoading: Boolean = true,
    val categories: List<SoundscapeCategory> = emptyList(),
    val showDemoButton: Boolean = true,
    val isDownloadingDemo: Boolean = false,
    val pendingComposerCategoryId: Long? = null,
    val errorMessage: String? = null,
)

@Composable
fun SoundscapeLibraryRoute(
    onOpenComposer: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.pendingComposerCategoryId) {
        uiState.pendingComposerCategoryId?.let { categoryId ->
            onOpenComposer(categoryId)
            viewModel.onComposerOpened()
        }
    }

    SoundscapeLibraryScreen(
        uiState = uiState,
        onCreateCategory = viewModel::createCategory,
        onDeleteCategory = viewModel::deleteCategory,
        onDownloadDemoSoundscapes = viewModel::downloadDemoSoundscapes,
        onOpenComposer = onOpenComposer,
        modifier = modifier,
    )
}

@Composable
fun SoundscapeLibraryScreen(
    uiState: SoundscapeLibraryUiState,
    onCreateCategory: (String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onDownloadDemoSoundscapes: () -> Unit,
    onOpenComposer: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable(uiState.errorMessage) { mutableStateOf(uiState.errorMessage) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Soundscape Library",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Select an atmospheric core to begin weaving your auditory tapestry.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (uiState.showDemoButton) {
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onClick = onDownloadDemoSoundscapes,
                            enabled = !uiState.isDownloadingDemo,
                        ) {
                            if (uiState.isDownloadingDemo) {
                                CircularProgressIndicator()
                            } else {
                                Text(text = "Get Demo Soundscapes")
                            }
                        }
                    }
                }
                if (uiState.categories.isEmpty()) {
                    item {
                        EmptyStateView(
                            illustration = Icons.Default.AutoStories,
                            title = "Create your first category",
                            actionLabel = "Create Category",
                            onActionClick = { showCreateDialog = true },
                        )
                    }
                } else {
                    items(items = uiState.categories, key = SoundscapeCategory::id) { category ->
                        SwipeToDeleteContainer(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onDelete = { onDeleteCategory(category.id) },
                        ) {
                            SoundscapeCategoryCard(
                                category = category,
                                onOpenComposer = { onOpenComposer(category.id) },
                            )
                        }
                    }
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { showCreateDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.height(0.dp))
                        Text(text = "Create Category")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (showCreateDialog) {
            CreateSoundscapeCategoryDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name ->
                    onCreateCategory(name)
                    showCreateDialog = false
                },
            )
        }

        ErrorDialog(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    onOpenComposer: () -> Unit,
) {
    BentoCard(
        onClick = onOpenComposer,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = category.themeLabel ?: "Custom",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic,
            )
            Text(
                text = "I: ${category.levelOneTrackCount} · II: ${category.levelTwoTrackCount} · III: ${category.levelThreeTrackCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                IconButton(onClick = onOpenComposer) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${category.name}",
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateSoundscapeCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var categoryName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create Category") },
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text(text = "Category name") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank(),
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
) : ViewModel() {
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        soundscapeRepository: SoundscapeRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(soundscapeRepository) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(SoundscapeLibraryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            combine(
                soundscapeRepository.observeCategories(),
                soundscapeRepository.observeHasDemoSoundscapes(),
            ) { categories, hasDemoSoundscapes ->
                _uiState.value.copy(
                    isLoading = false,
                    categories = categories,
                    showDemoButton = !hasDemoSoundscapes,
                )
            }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load soundscapes.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun createCategory(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }
        viewModelScope.launch(mainDispatcher) {
            val categoryId = soundscapeRepository.createCategory(trimmedName)
            _uiState.value = _uiState.value.copy(pendingComposerCategoryId = categoryId)
        }
    }

    fun onComposerOpened() {
        _uiState.value = _uiState.value.copy(pendingComposerCategoryId = null)
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch(mainDispatcher) {
            soundscapeRepository.deleteCategory(categoryId)
        }
    }

    fun downloadDemoSoundscapes() {
        viewModelScope.launch(mainDispatcher) {
            _uiState.value = _uiState.value.copy(isDownloadingDemo = true)
            soundscapeRepository.seedDemoSoundscapes()
            _uiState.value = _uiState.value.copy(
                isDownloadingDemo = false,
                showDemoButton = false,
            )
        }
    }
}
