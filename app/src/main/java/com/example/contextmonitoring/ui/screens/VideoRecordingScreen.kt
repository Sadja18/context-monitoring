package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoRecordingScreen(
    onBackClick: () -> Unit,
    onRecordingComplete: (String) -> Unit, // Returns video path
    modifier: Modifier = Modifier
) {
    // Recording state
    var isRecording by remember { mutableStateOf(false) }
    var recordedVideoPath by remember { mutableStateOf<String?>(null) }
    var currentTime by remember { mutableIntStateOf(0) }
    val maxDuration = 45 // seconds
    val scope = rememberCoroutineScope()

    // Timer logic
    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            while (currentTime < maxDuration) {
                delay(1000) // Update every second
                currentTime++
            }
            // Recording finished
            isRecording = false

            // Generate unique filename
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val randomSuffix = (1000..9999).random()
//            recordedVideoPath = "video_${timestamp}_${randomSuffix}.mp4"
            recordedVideoPath = "video_mock.mp4"
            onRecordingComplete(recordedVideoPath!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Video Recording",
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Video Preview Area
            VideoPreviewArea(
                isRecording = isRecording,
                recordedVideoPath = recordedVideoPath
            )

            // Timer Display
            Text(
                text = "${String.format(Locale.getDefault(), "%02d", currentTime)}/${maxDuration}s",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isRecording) MaterialTheme.colorScheme.primary else Color.Gray
            )

            // Action Buttons
            if (recordedVideoPath == null) {
                // Start Recording Button
                Button(
                    onClick = {
                        isRecording = true
                    },
                    enabled = !isRecording,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Capture", fontSize = 18.sp)
                }
            } else {
                // Playback and Delete Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Playback Button
                    OutlinedButton(
                        onClick = {
                            // TODO: Implement actual playback
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Play")
                    }

                    // Delete Button
                    Button(
                        onClick = {
                            recordedVideoPath = null
                            currentTime = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPreviewArea(
    isRecording: Boolean,
    recordedVideoPath: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                if (recordedVideoPath != null) {
                    Color(0xFF212121) // Dark background for "video"
                } else {
                    Color(0xFF424242) // Darker background for preview
                }
            )
            .border(2.dp, if (isRecording) Color.Red else Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        if (recordedVideoPath != null) {
            // Show "recorded video" placeholder
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Recorded video",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Video Recorded",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            // Show camera preview placeholder
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Red, CircleShape)
                    )
                }
                Text(
                    text = if (isRecording) "Recording..." else "Camera Preview",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}