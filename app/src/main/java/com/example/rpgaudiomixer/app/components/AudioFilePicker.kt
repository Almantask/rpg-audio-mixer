package com.example.rpgaudiomixer.app.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun AudioFilePickerButton(
    text: String,
    onAudioPicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                onAudioPicked(uri.toString())
            }
        },
    )
    val openPicker = remember(openDocumentLauncher) {
        {
            openDocumentLauncher.launch(arrayOf("audio/*"))
        }
    }

    Button(
        modifier = modifier,
        onClick = openPicker,
    ) {
        Text(text = text)
    }
}
