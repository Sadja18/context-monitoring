package com.example.contextmonitoring.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.ui.BuildConfig
import com.example.contextmonitoring.ui.components.CameraXPreview
import com.example.contextmonitoring.ui.components.VideoPlayer
import com.example.contextmonitoring.utils.CameraXVideoRecorder
import com.example.contextmonitoring.utils.FileUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoRecordingScreen(
    onBackClick: () -> Unit,
    onRecordingComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var recordedVideoPath by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(0) }
    val maxDuration = if(BuildConfig.DEBUG) 5 else 45 // demo seconds
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraRecorder = remember { CameraXVideoRecorder(context, lifecycleOwner) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }
            isRecording = false
            // Save demo audio file
            val videoFile = FileUtils.createVideoFile(context)
            recordedVideoPath = videoFile.absolutePath
            onRecordingComplete(recordedVideoPath!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Recording", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Show camera preview until a video is recorded
            if (recordedVideoPath == null) {
                CameraXPreview(
                    cameraRecorder = cameraRecorder,
                    modifier = Modifier.weight(1f)
                ) { uri ->
                    recordedVideoPath = uri
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
