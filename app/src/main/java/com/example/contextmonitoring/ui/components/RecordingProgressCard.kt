package com.example.contextmonitoring.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class RecordingProgress(
    val heartRateCompleted: Boolean = false,
    val respiratoryCompleted: Boolean = false,
    val symptomsCompleted: Boolean = false
)

@Composable
fun RecordingProgressCard(
    progress: RecordingProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(bottom = 16.dp), // Space before the cards below
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Current recording session",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Progress Items
            RecordingProgressItem(
                label = "Heart Rate Video",
                completed = progress.heartRateCompleted,
                color = Color(0xFFF44336) // Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            RecordingProgressItem(
                label = "Respiratory Audio",
                completed = progress.respiratoryCompleted,
                color = Color(0xFF4CAF50) // Green
            )

            Spacer(modifier = Modifier.height(8.dp))

            RecordingProgressItem(
                label = "Symptoms",
                completed = progress.symptomsCompleted,
                color = Color(0xFFFFC107) // Yellow/Amber
            )
        }
    }
}

@Composable
private fun RecordingProgressItem(
    label: String,
    completed: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (completed) {
                color
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            fontWeight = if (completed) FontWeight.Medium else FontWeight.Normal
        )

        Icon(
            imageVector = if (completed) {
                Icons.Default.CheckCircle
            } else {
                Icons.Default.Circle
            },
            contentDescription = if (completed) "Completed" else "Not completed",
            tint = if (completed) {
                color
            } else {
                MaterialTheme.colorScheme.outline
            },
            modifier = Modifier.size(24.dp)
        )
    }
}