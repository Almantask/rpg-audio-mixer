package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState

@Composable
fun AddFxToSceneScreen(
    sceneId: Long,
    onBack: () -> Unit,
    viewModel: AddFxToSceneViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState(sceneId).collectAsStateWithLifecycle()
    val addedIds = remember { mutableStateOf(emptySet<Long>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(
            onCredits = {},
            showBack = true,
            onBack = onBack,
        )

        Text(
            text = "Add FX",
            style = MaterialTheme.typography.displaySmall,
            color = ArcanumGold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Text(
            text = "Tap + to add an FX sound to this scene's soundboard.",
            style = MaterialTheme.typography.bodyMedium,
            color = ArcanumGrayLight,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        )

        if (state.effects.isEmpty() && !state.isLoading) {
            EmptyState(
                title = "No FX Available",
                subtitle = "Go to Library → Sound Effects to import FX.",
            )
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(state.effects, key = { it.id }) { effect ->
                FxEffectRow(
                    effect = effect,
                    added = effect.id in addedIds.value,
                    onAdd = {
                        viewModel.addFx(sceneId, effect.id)
                        addedIds.value = addedIds.value + effect.id
                    },
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FxEffectRow(
    effect: FxEffect,
    added: Boolean,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumCardSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = effect.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            if (effect.tags.isNotEmpty()) {
                Text(
                    text = effect.tags.joinToString(", ").uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGrayMid,
                )
            }
        }
        IconButton(
            onClick = { if (!added) onAdd() },
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = if (added) "Added" else "Add ${effect.name}",
                tint = if (added) ArcanumGold else ArcanumGrayLight,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
