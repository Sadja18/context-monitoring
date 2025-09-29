package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contextmonitoring.ui.components.CameraXPreview
import com.example.contextmonitoring.ui.components.VideoPlayer
import com.example.contextmonitoring.utils.CameraXVideoRecorder
import kotlinx.coroutines.delay

import com.example.contextmonitoring.BuildConfig


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoRecordingScreen(
    existingVideoPath: String?,
    onBackClick: () -> Unit,
    onRecordingComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    println("Existing video path $existingVideoPath")
    var recordedVideoPath by remember { mutableStateOf(
        if (!existingVideoPath.isNullOrBlank()) existingVideoPath else null
    ) }

    var isRecording by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(0) }
    val maxDuration = if(BuildConfig.DEBUG) 5 else 45 // demo seconds
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraRecorder = remember { CameraXVideoRecorder(context, lifecycleOwner) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            cameraRecorder.startRecording { path ->
                recordedVideoPath = path
                onRecordingComplete(path)
            }

            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }

            cameraRecorder.stopRecording()
            isRecording = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Recording", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Show camera preview until a video is recorded
            if (recordedVideoPath == null) {
                // Preview
                CameraXPreview(
                    cameraRecorder = cameraRecorder,
                    modifier = Modifier.weight(1f)
                ) { uri ->
                    recordedVideoPath = uri
                }

                // Timer while recording
                if (isRecording) {
                    Text(
                        text = "${currentTime}s / ${maxDuration}s",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

                // Record button (disabled while recording)
                Button(
                    onClick = { isRecording = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isRecording
                ) {
                    Text("Start Recording")
                }
            } else {
                // Show video playback
                VideoPlayer(videoUri = recordedVideoPath!!)

                // Optional: Button to record again
                Button(
                    onClick = { recordedVideoPath = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Record Again")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
        }
    }
}
