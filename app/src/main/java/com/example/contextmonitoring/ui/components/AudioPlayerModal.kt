package com.example.contextmonitoring.ui.components

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerModal(
    audioPath: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    val mediaPlayer = remember { MediaPlayer() }

    // Coroutine scope for updating progress
    val scope = rememberCoroutineScope()

    // Initialize and prepare media player
    LaunchedEffect(audioPath) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioPath)
            mediaPlayer.prepare()
            duration = mediaPlayer.duration / 1000 // in seconds
            currentPosition = 0
        } catch (e: Exception) {
            e.printStackTrace()
            onDismiss() // or show error
        }
    }

    // Start/stop progress tracking
    LaunchedEffect(isPlaying) {
        if (isPlaying && mediaPlayer.isPlaying) {
            while (isActive && mediaPlayer.isPlaying) {
                currentPosition = mediaPlayer.currentPosition / 1000
                delay(1000)
            }
            if (!mediaPlayer.isPlaying) {
                isPlaying = false
                currentPosition = mediaPlayer.currentPosition / 1000
            }
        }
    }

    // Handle play/pause
    fun togglePlayback() {
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        } else {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    // Clean up on dismiss
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Playing Audio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Progress bar
            LinearProgressIndicator(
                progress = { if (duration > 0) currentPosition.toFloat() / duration else 0f },
                modifier = Modifier.fillMaxWidth(),
            )

            // Time info
            Text(
                text = "${currentPosition}s / ${duration}s",
                style = MaterialTheme.typography.bodyMedium
            )

            // Play/Pause button
            IconButton(
                onClick = { togglePlayback() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Close button
            FilledTonalButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    }
}