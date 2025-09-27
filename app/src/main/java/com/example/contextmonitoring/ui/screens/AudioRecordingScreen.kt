package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contextmonitoring.ui.components.AudioRecordingCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    onBackClick: () -> Unit,
    onRecordingComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioPath by remember { mutableStateOf<String?>(null) }
    var currentTime by remember { mutableStateOf(0) }
    val maxDuration = 45 // seconds

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }
            isRecording = false
            recordedAudioPath = "mock_audio_path.mp3"
            onRecordingComplete(recordedAudioPath!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Audio Recording",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Audio Recording Card
            AudioRecordingCard(
                isRecording = isRecording,
                recordedAudioPath = recordedAudioPath,
                currentTime = currentTime,
                maxDuration = maxDuration,
                onStartRecording = { isRecording = true },
                onPlaybackClick = { /* TODO: Implement playback */ },
                onDeleteClick = {
                    recordedAudioPath = null
                    currentTime = 0
                }
            )
        }
    }
}