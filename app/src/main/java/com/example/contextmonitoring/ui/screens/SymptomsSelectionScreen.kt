package com.example.contextmonitoring.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contextmonitoring.data.SymptomsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsSelectionScreen(
    onBackClick: () -> Unit,
    onSaveSymptoms: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSymptoms by remember { mutableStateOf<Set<String>>(emptySet()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Symptoms",
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
                },
                actions = {
                    // Save button in top app bar
                    IconButton(
                        onClick = {
                            onSaveSymptoms(selectedSymptoms.toList())
                        },
                        enabled = selectedSymptoms.isNotEmpty()
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedSymptoms.isNotEmpty()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SymptomsData.symptomOptions) { symptom ->
                SymptomItem(
                    symptom = symptom,
                    isSelected = selectedSymptoms.contains(symptom.id),
                    onToggle = { isSelected ->
                        selectedSymptoms = if (isSelected) {
                            selectedSymptoms + symptom.id
                        } else {
                            selectedSymptoms - symptom.id
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SymptomItem(
    symptom: com.example.contextmonitoring.data.SymptomOption,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = symptom.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Icon(
                imageVector = if (isSelected) {
                    Icons.Default.CheckBox
                } else {
                    Icons.Default.CheckBoxOutlineBlank
                },
                contentDescription = if (isSelected) "Selected" else "Not selected",
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        }
    }
}