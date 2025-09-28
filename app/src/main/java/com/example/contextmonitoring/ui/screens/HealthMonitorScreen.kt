package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contextmonitoring.data.RecordingSession
import com.example.contextmonitoring.ui.components.HealthCard
import com.example.contextmonitoring.ui.components.RecordHistoryCard
import com.example.contextmonitoring.ui.components.RecordingProgress
import com.example.contextmonitoring.ui.components.RecordingProgressCard
import com.example.contextmonitoring.ui.components.SymptomsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMonitorScreen(
    onBackClick: () -> Unit,
    onHeartRateClick: () -> Unit,
    onRespiratoryClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    recordingProgress: RecordingProgress = RecordingProgress(),
    savedSessions: List<RecordingSession> = emptyList(),
    modifier: Modifier = Modifier
) {
    // Use Scaffold to properly handle TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health Monitor",
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
        },
        modifier = modifier
    ) { paddingValues ->
        // Body content with proper padding
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (recordingProgress.heartRateCompleted || recordingProgress.respiratoryCompleted || recordingProgress.symptomsCompleted) {
                item {
                    RecordingProgressCard(
                        progress = recordingProgress
                    )
                }
            }
            // Two Cards in a Row
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Heart Rate Card
                    HealthCard(
                        icon = Icons.Default.Favorite,
                        iconTint = Color(0xFFF44336), // Red (Material Red 500)
                        title = "Heart Rate",
                        record = if (recordingProgress.heartRateCompleted) "Completed" else "Record",
                        greyText = "Last recorded: 2 min ago",
                        modifier = Modifier.weight(1f),
                        cardBackgroundColor = Color(0xFFFFF8F8), // Very light red background
                        onClick = onHeartRateClick,
                    )

                    // Respiratory Card
                    HealthCard(
                        icon = Icons.Default.Air,
                        iconTint = Color(0xFF4CAF50), // Green (Material Green 500)
                        title = "Respiratory",
                        record = if (recordingProgress.respiratoryCompleted) "Completed" else "Record",
                        greyText = "Last recorded: 5 min ago",
                        modifier = Modifier.weight(1f),
                        cardBackgroundColor = Color(0xFFF8FFF8), // Very light green background
                        onClick = onRespiratoryClick
                    )
                }
            }

            // One card in a Row
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Full-width Symptoms Card - YELLOWISH
                    SymptomsCard(
                        icon = Icons.Default.MedicalServices,
                        iconTint = Color(0xFFFFC107), // Amber/Yellow (Material Amber 500)
                        title = "Symptoms",
                        subtitle = "Track and monitor your current symptoms",
                        onLogSymptomsClick = onSymptomsClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        cardBackgroundColor = Color(0xFFFFFDF8), // Very light yellow/amber background
                    )
                }
            }

            // Create New Record Button (show when all completed)
            if (recordingProgress.heartRateCompleted &&
                recordingProgress.respiratoryCompleted &&
                recordingProgress.symptomsCompleted
            ) {
                item{
                    Button(
                        onClick = {
                            println("Saving session recorded $recordingProgress")

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Create New Record")
                    }
                }
            }

            // RECORD HISTORY SECTION
            if (savedSessions.isNotEmpty()) {

                // Show saved sessions (most recent first)
                // Use items() for multiple history cards
                // Record History Section
                if (savedSessions.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }

                    item {
                        Text(
                            text = "Record History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Correct items() usage with key parameter
                    items(
                        items = savedSessions.reversed(),
                        key = { session -> session.id } // Unique key for each session
                    ) { session ->
                        RecordHistoryCard(
                            session = session
                        )
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthMonitorScreenPreview() {
    com.example.contextmonitoring.ui.theme.ContextMonitoringTheme {
        HealthMonitorScreen(
            onBackClick = {},
            onHeartRateClick = {},
            onRespiratoryClick = {},
            onSymptomsClick = {}
        )
    }
}