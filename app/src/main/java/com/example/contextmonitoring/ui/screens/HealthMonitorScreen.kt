package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.contextmonitoring.ui.components.HealthCard
import com.example.contextmonitoring.ui.components.SymptomsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMonitorScreen(
    onBackClick: () -> Unit,
    onHeartRateClick: () -> Unit,
    onRespiratoryClick: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Two Cards in a Row
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
                    record = "Record",
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
                    record = "Record",
                    greyText = "Last recorded: 5 min ago",
                    modifier = Modifier.weight(1f),
                    cardBackgroundColor = Color(0xFFF8FFF8), // Very light green background
                    onClick = onRespiratoryClick
                )
            }

            // One card in a Row
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
                    onLogSymptomsClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    cardBackgroundColor = Color(0xFFFFFDF8), // Very light yellow/amber background
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthMonitorScreenPreview() {
    com.example.contextmonitoring.ui.theme.ContextMonitoringTheme {
        HealthMonitorScreen(onBackClick = {}, onHeartRateClick = {}, onRespiratoryClick = {})
    }
}