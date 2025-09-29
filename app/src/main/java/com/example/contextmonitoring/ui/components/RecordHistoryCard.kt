package com.example.contextmonitoring.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contextmonitoring.data.RecordingSession
import com.example.contextmonitoring.utils.rememberMedia3Helper

@Composable
fun RecordHistoryCard(
    session: RecordingSession,
    modifier: Modifier = Modifier
) {
    val media3Helper = rememberMedia3Helper()
    val context = LocalContext.current

    // State to track which video is being played (null = none)
    var playingVideoUri by remember { mutableStateOf<String?>(null) }
    var playingAudioUri by remember { mutableStateOf<String?>(null) }

    // Dismiss modal
    fun dismissVideo() {
        playingVideoUri = null
    }

    // Dismiss modal2
    fun dismissAudio() {
        playingAudioUri = null
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Session Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session #${session.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = session.getFormattedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Heart Rate Section
            SessionComponentItem(
                title = "Heart Rate",
                hasContent = session.heartRateVideoPath != null,
                color = Color(0xFFF44336),
                onPlayClick = {
                    dismissVideo()
                    dismissAudio()
                    session.heartRateVideoPath?.let { path ->
                        playingVideoUri = path // Show modal
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Respiratory Section
            SessionComponentItem(
                title = "Respiratory",
                hasContent = session.respiratoryAudioPath != null,
                color = Color(0xFF4CAF50),
                onPlayClick = {
                    dismissVideo()
                    dismissAudio()
                    session.respiratoryAudioPath?.let { path ->
                        playingAudioUri = path
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Symptoms Section
            Column {
                Text(
                    text = "Symptoms",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFC107)
                )
                Text(
                    text = if (session.symptoms.isNotEmpty()) {
                        session.getSymptomLabels()
                    } else {
                        "No symptoms recorded"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Show video player in modal when needed
    playingVideoUri?.let { uri ->
        VideoPlayerModal(
            videoUri = uri,
            onDismiss = ::dismissVideo
        )
    }

    // Show audio player in modal when needed
    playingAudioUri?.let{uri ->
        AudioPlayerModal(
            audioPath = uri,
            onDismiss = ::dismissAudio
        )
    }
}

@Composable
private fun SessionComponentItem(
    title: String,
    hasContent: Boolean,
    color: Color,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = color
            )
            if (!hasContent) {
                Text(
                    text = "Not recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }

        if (hasContent) {
            OutlinedButton(
                onClick = onPlayClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}