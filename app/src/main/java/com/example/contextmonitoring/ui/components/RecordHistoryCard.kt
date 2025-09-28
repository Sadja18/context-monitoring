package com.example.contextmonitoring.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contextmonitoring.data.RecordingSession

import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import com.example.contextmonitoring.utils.rememberMedia3Helper

@Composable
fun RecordHistoryCard(
    session: RecordingSession,
    modifier: Modifier = Modifier
) {
    val media3Helper = rememberMedia3Helper()
    val context = LocalContext.current

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
                title = "Heart Rate Video",
                hasContent = session.heartRateVideoPath != null,
                color = Color(0xFFF44336),
                onPlayClick = {
                    // session.heartRateVideoPath?.let { onVideoPlay(it) }
                    session.heartRateVideoPath?.let { path ->
                        media3Helper.playMedia(path)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Respiratory Section
            SessionComponentItem(
                title = "Respiratory Audio",
                hasContent = session.respiratoryAudioPath != null,
                color = Color(0xFF4CAF50),
                onPlayClick = {
                    session.respiratoryAudioPath?.let { path ->
                        media3Helper.playMedia(path)
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