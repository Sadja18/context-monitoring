package com.example.contextmonitoring.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.media3.ui.BuildConfig
import com.example.contextmonitoring.utils.FileUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    onBackClick: () -> Unit,
    onRecordingComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioPath by remember { mutableStateOf<String?>(null) }
    var currentTime by remember { mutableStateOf(0) }
    val maxDuration = if(BuildConfig.DEBUG) 5 else 45 // demo seconds

    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }
            isRecording = false
            // Save demo audio file
            val audioFile = FileUtils.createAudioFile(context)
            recordedAudioPath = audioFile.absolutePath
            onRecordingComplete(recordedAudioPath!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audio Recording", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { isRecording = !isRecording },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (recordedAudioPath != null) {
                IconButton(onClick = {
                    if (!isPlaying) {
                        mediaPlayer.apply {
                            reset()
                            setDataSource(recordedAudioPath)
                            prepare()
                            start()
                        }
                        isPlaying = true
                        mediaPlayer.setOnCompletionListener {
                            isPlaying = false
                        }
                    } else {
                        mediaPlayer.pause()
                        isPlaying = false
                    }
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause"
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}
