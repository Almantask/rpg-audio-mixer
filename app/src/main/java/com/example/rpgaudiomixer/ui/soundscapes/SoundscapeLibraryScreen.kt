package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.domain.trash.SoundscapeCategoryTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

object SoundscapeLibraryTestTags {
    const val EMPTY_ILLUSTRATION = "Soundscapes_Empty_Illustration"
    const val CREATE_BUTTON = "Soundscapes_Create_Button"
    const val CREATE_DIALOG = "Soundscapes_Create_Dialog"
    const val NAME_INPUT = "Soundscapes_Create_Name"
    const val DEMO_BUTTON = "Soundscapes_Demo_Button"
    const val DEMO_LOADING = "Soundscapes_Demo_Loading"

    fun card(name: String): String = "Soundscapes_Card_${name.asTagSuffix()}"
    fun editButton(name: String): String = "Soundscapes_Edit_${name.asTagSuffix()}"
}

data class SoundscapeLibraryUiState(
    val isLoading: Boolean = true,
    val categories: List<SoundscapeCategory> = emptyList(),
    val showCreateDialog: Boolean = false,
    val draftName: String = "",
    val showDemoButton: Boolean = true,
    val isDownloadingDemo: Boolean = false,
    val navigateToCategoryId: Long? = null,
    val errorMessage: String? = null,
)

@Composable
fun SoundscapeLibraryRoute(
    onOpenComposer: (Long) -> Unit,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToCategoryId) {
        uiState.navigateToCategoryId?.let { categoryId ->
            onOpenComposer(categoryId)
            viewModel.onComposerNavigationHandled()
        }
    }

    SoundscapeLibraryScreen(
        uiState = uiState,
        onOpenCreateDialog = viewModel::openCreateDialog,
        onDraftNameChange = viewModel::updateDraftName,
        onConfirmCreate = viewModel::confirmCreateCategory,
        onDismissCreate = viewModel::dismissCreateDialog,
        onDownloadDemo = viewModel::downloadDemoSoundscapes,
        onOpenCategory = { category -> onOpenComposer(category.id) },
        onDeleteCategory = viewModel::deleteCategory,
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun SoundscapeLibraryScreen(
    uiState: SoundscapeLibraryUiState,
    onOpenCreateDialog: () -> Unit,
    onDraftNameChange: (String) -> Unit,
    onConfirmCreate: () -> Unit,
    onDismissCreate: () -> Unit,
    onDownloadDemo: () -> Unit,
    onOpenCategory: (SoundscapeCategory) -> Unit,
    onDeleteCategory: (SoundscapeCategory) -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                LibraryHeader()
            }
            if (uiState.showDemoButton) {
                item {
                    DemoButtonCard(isLoading = uiState.isDownloadingDemo, onDownloadDemo = onDownloadDemo)
                }
            }
            if (uiState.categories.isEmpty()) {
                item {
                    EmptyLibraryState(onCreateCategory = onOpenCreateDialog)
                }
            } else {
                items(uiState.categories, key = SoundscapeCategory::id) { category ->
                    SwipeToDeleteCategoryContainer(category = category, onDeleteCategory = onDeleteCategory) {
                        SoundscapeCategoryCard(
                            category = category,
                            onOpenCategory = onOpenCategory,
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag(SoundscapeLibraryTestTags.CREATE_BUTTON),
            onClick = onOpenCreateDialog,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Create Category")
        }

        if (uiState.showCreateDialog) {
            AlertDialog(
                modifier = Modifier.testTag(SoundscapeLibraryTestTags.CREATE_DIALOG),
                onDismissRequest = onDismissCreate,
                title = { Text("Create Category") },
                text = {
                    OutlinedTextField(
                        modifier = Modifier.testTag(SoundscapeLibraryTestTags.NAME_INPUT),
                        value = uiState.draftName,
                        onValueChange = onDraftNameChange,
                        singleLine = true,
                        label = { Text("Category name") },
                    )
                },
                confirmButton = { TextButton(onClick = onConfirmCreate) { Text("Confirm") } },
                dismissButton = { TextButton(onClick = onDismissCreate) { Text("Cancel") } },
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@Composable
private fun LibraryHeader() {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Soundscape Library",
            style = MaterialTheme.typography.headlineMedium,
            color = ArcanumGold,
            fontWeight = FontWeight.Bold,
        )
        Text("Select an atmospheric core to begin weaving your auditory tapestry...")
    }
}

@Composable
private fun DemoButtonCard(
    isLoading: Boolean,
    onDownloadDemo: () -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("100 Free Demo Soundscapes", color = ArcanumGold, fontWeight = FontWeight.Bold)
                Text("Quickly populate your library with atmospheric starters.")
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .testTag(SoundscapeLibraryTestTags.DEMO_LOADING),
                )
            } else {
                FilledTonalButton(
                    modifier = Modifier.testTag(SoundscapeLibraryTestTags.DEMO_BUTTON),
                    onClick = onDownloadDemo,
                ) {
                    Text("Get Demo Soundscapes")
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryState(
    onCreateCategory: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                )
                .testTag(SoundscapeLibraryTestTags.EMPTY_ILLUSTRATION),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ArcanumGold, modifier = Modifier.size(48.dp))
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "No soundscape categories yet",
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Create your first category to begin composing layered ambience.",
        )
        FilledTonalButton(modifier = Modifier.padding(top = 20.dp), onClick = onCreateCategory) {
            Text("Create Category")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteCategoryContainer(
    category: SoundscapeCategory,
    onDeleteCategory: (SoundscapeCategory) -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteCategory(category)
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text("Move to Trash", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = content,
    )
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    onOpenCategory: (SoundscapeCategory) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(SoundscapeLibraryTestTags.card(category.name))
            .clickable { onOpenCategory(category) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(category.themeLabel ?: "ATMOSPHERE", fontWeight = FontWeight.SemiBold)
                    Text(category.name, color = ArcanumGold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    modifier = Modifier.testTag(SoundscapeLibraryTestTags.editButton(category.name)),
                    onClick = { onOpenCategory(category) },
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit ${category.name}")
                }
            }
            Text(category.trackCountLabel())
        }
    }
}

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val repository: SoundscapeRepository,
    private val trashRepository: SoundscapeCategoryTrashRepository,
    private val trashVaultRepository: TrashVaultRepository,
) : ViewModel() {
    private val draftState = MutableStateFlow(LibraryDraft())
    private val isDownloadingDemo = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(SoundscapeLibraryUiState())
    val uiState: StateFlow<SoundscapeLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observeCategories(),
                repository.hasDemoSoundscapes(),
                draftState,
                isDownloadingDemo,
            ) { categories, hasDemoContent, draft, downloading ->
                SoundscapeLibraryUiState(
                    isLoading = false,
                    categories = categories,
                    showCreateDialog = draft.isCreateDialogOpen,
                    draftName = draft.name,
                    showDemoButton = !hasDemoContent,
                    isDownloadingDemo = downloading,
                    navigateToCategoryId = draft.navigateToCategoryId,
                    errorMessage = draft.errorMessage,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun openCreateDialog() {
        draftState.value = LibraryDraft(isCreateDialogOpen = true)
    }

    fun dismissCreateDialog() {
        draftState.value = draftState.value.copy(isCreateDialogOpen = false, name = "", errorMessage = null)
    }

    fun updateDraftName(name: String) {
        draftState.update { it.copy(name = name, errorMessage = null) }
    }

    fun confirmCreateCategory() {
        val draftName = draftState.value.name.trim()
        if (draftName.isBlank()) {
            draftState.update { it.copy(errorMessage = "Every category needs a name.") }
            return
        }

        viewModelScope.launch {
            val categoryId = repository.createCategory(draftName)
            draftState.value = LibraryDraft(navigateToCategoryId = categoryId)
        }
    }

    fun onComposerNavigationHandled() {
        draftState.value = LibraryDraft()
    }

    fun deleteCategory(category: SoundscapeCategory) {
        viewModelScope.launch {
            trashVaultRepository.trashSoundscapeCategory(category.id)
            repository.deleteCategory(category.id)
            trashRepository.recordDeletedCategory(category.name)
        }
    }

    fun downloadDemoSoundscapes() {
        viewModelScope.launch {
            isDownloadingDemo.value = true
            // Demo seeding is local and otherwise instant, so keep the spinner visible briefly
            // to communicate progress when populating the starter content.
            delay(150)
            repository.downloadDemoSoundscapes()
            isDownloadingDemo.value = false
        }
    }

    fun clearError() {
        draftState.update { it.copy(errorMessage = null) }
    }
}

private data class LibraryDraft(
    val isCreateDialogOpen: Boolean = false,
    val name: String = "",
    val navigateToCategoryId: Long? = null,
    val errorMessage: String? = null,
)

private fun SoundscapeCategory.trackCountLabel(): String = IntensityLevel.entries.joinToString(" · ") { intensity ->
    "${intensity.label}: ${countFor(intensity)}"
}

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')
