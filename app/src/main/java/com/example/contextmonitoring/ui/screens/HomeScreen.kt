package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// ADD THIS IMPORT for Material Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star

// ADD THIS IMPORT for Preview
import androidx.compose.ui.tooling.preview.Preview

// ADD THIS IMPORT for icon on top
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Android
import androidx.compose.ui.draw.clip

@Composable
fun HomeScreen(
    onVitalCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Android,
            contentDescription = "App icon",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            tint = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Context Monitoring",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Subtitle
        Text(
            text = "Monitor and manage your captured data",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Vital Capture Button
        ElevatedButton(
            onClick = onVitalCaptureClick, // navigate to health screen
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color(0xFF2196F3), // Blue background
                contentColor = Color.White // White text
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star, // Material Icon
                contentDescription = "Star icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Vital Capture",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete All Button
        OutlinedButton(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFFF44336), // Red text
                // borderColor is handled automatically by outlinedButtonColors
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete, // Material Icon
                contentDescription = "Delete icon",
                tint = Color(0xFFF44336), // Red icon
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Delete all captured",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // You need to wrap in your theme for proper styling
    com.example.contextmonitoring.ui.theme.ContextMonitoringTheme {
        HomeScreen(onVitalCaptureClick = {}) // empty lambda for preview
    }
}