So, now my UI is
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
import com.example.contextmonitoring.utils.AudioRecorderUtil
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
    val audioRecorder = remember { AudioRecorderUtil(context) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            val audioFile = FileUtils.createAudioFile(context)

            audioRecorder.startRecording(audioFile) { path ->
                recordedAudioPath = path
                onRecordingComplete(path)
            }

            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }

            // stop after maxDuration
            audioRecorder.stopRecording()
            isRecording = false
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
---
However, I also intend to use this UI
package com.example.contextmonitoring.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun AudioRecordingCard(
isRecording: Boolean,
recordedAudioPath: String?,
currentTime: Int,
maxDuration: Int,
onStartRecording: () -> Unit,
onPlaybackClick: () -> Unit,
onDeleteClick: () -> Unit,
modifier: Modifier = Modifier
) {
Card(
modifier = modifier
.fillMaxWidth()
.height(300.dp)
.padding(16.dp),
shape = MaterialTheme.shapes.medium,
elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
colors = CardDefaults.cardColors(
containerColor = Color(0xFFD8FAD9)
)
) {
Column(
modifier = Modifier
.fillMaxSize()
.padding(24.dp),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.spacedBy(24.dp)
) {
Box(
modifier = Modifier
.size(48.dp)
.background(Color(0xFF4CAF50).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
contentAlignment = Alignment.Center
) {
Icon(
imageVector = Icons.Default.Air,
contentDescription = "Respiratory",
tint = Color(0xFF4CAF50),
modifier = Modifier.size(24.dp)
)
}

            // Status Text
            if (recordedAudioPath != null) {
                Text(
                    text = "Recording Complete",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else if (isRecording) {
                Text(
                    text = "Recording... ${
                        String.format(
                            Locale.getDefault(),
                            "%02d",
                            currentTime
                        )
                    }/${maxDuration}s",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Ready to record audio",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Action Buttons
            if (recordedAudioPath != null) {
                // Playback and Delete Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onPlaybackClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Play Audio")
                    }

                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }
            } else {
                // Start Recording Button
                OutlinedButton(
                    onClick = onStartRecording,
                    enabled = !isRecording, // Disabled during recording
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Recording")
                }
            }
        }
    }
}
so, should I include playback and record feature inside the card. if yes, would the launhed effect of screen change?
