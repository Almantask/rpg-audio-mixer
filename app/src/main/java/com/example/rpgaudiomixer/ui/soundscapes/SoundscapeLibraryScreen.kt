package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog

@Composable
fun SoundscapeLibraryScreen(
    onNavigateToComposer: (Long) -> Unit = {},
    onNavigateToCredits: () -> Unit = {},
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Soundscape Library",
                onGearClick = onNavigateToCredits
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("NewCategoryFAB")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Composition",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SoundscapeLibraryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SoundscapeLibraryUiState.Success -> {
                    if (state.categoriesWithCounts.isEmpty()) {
                        EmptySoundscapeLibraryState(
                            onCreateCategory = { viewModel.showCreateDialog() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        SoundscapeCategoriesGrid(
                            categoriesWithCounts = state.categoriesWithCounts,
                            onCategoryClick = { onNavigateToComposer(it.category.id) },
                            onEditClick = { onNavigateToComposer(it.category.id) },
                            onDeleteCategory = { viewModel.deleteCategory(it.category) }
                        )
                    }
                }
                is SoundscapeLibraryUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name ->
                viewModel.createCategory(name)
            }
        )
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = { viewModel.clearError() }
    )
}

@Composable
private fun SoundscapeCategoriesGrid(
    categoriesWithCounts: List<CategoryWithCounts>,
    onCategoryClick: (CategoryWithCounts) -> Unit,
    onEditClick: (CategoryWithCounts) -> Unit,
    onDeleteCategory: (CategoryWithCounts) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categoriesWithCounts, key = { it.category.id }) { categoryWithCounts ->
            SoundscapeCategoryCard(
                categoryName = categoryWithCounts.category.name,
                countI = categoryWithCounts.countI,
                countII = categoryWithCounts.countII,
                countIII = categoryWithCounts.countIII,
                onEditClick = { onEditClick(categoryWithCounts) },
                onCardClick = { onCategoryClick(categoryWithCounts) }
            )
        }
    }
}

@Composable
private fun EmptySoundscapeLibraryState(
    onCreateCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
            .testTag("EmptySoundscapeLibraryState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎵",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Soundscape Categories Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first composition to build atmospheric soundscapes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
