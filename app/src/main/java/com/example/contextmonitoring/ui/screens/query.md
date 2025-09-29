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
onCreateNewRecordingSessionClick: ()->Unit,
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
                            onCreateNewRecordingSessionClick()

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
onSymptomsClick = {},
onCreateNewRecordingSessionClick = {}
)
}
}
----
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
---
package com.example.contextmonitoring.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contextmonitoring.data.RecordingRepository
import com.example.contextmonitoring.data.RecordingSession
import com.example.contextmonitoring.ui.components.RecordingProgress
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime

class HealthMonitorViewModel(application: Application) : AndroidViewModel(application) {
private val repository = RecordingRepository.getInstance(application)

    var recordingProgress by mutableStateOf(RecordingProgress())
        private set

    // Mock saved sessions (replace with real database later)
    var savedSessions by mutableStateOf<List<RecordingSession>>(emptyList())
        private set

    var currentDraftId by mutableStateOf<Long?>(null)
        private set

    var currentDraftAudioPath by mutableStateOf<String?>(null)
        private set

    var currentDraftVideoPath by mutableStateOf<String?>(null)
        private set

    var currentDraftSymptoms by mutableStateOf<List<String>>(emptyList())
        private set

    private fun loadCurrentDraft() {
        viewModelScope.launch {
            val draft = repository.getCurrentDraft()
            if (draft != null) {
                println("Draft object: $draft")
                println("Draft symptoms raw value: ${draft.symptoms} (${draft.symptoms.javaClass})")

                currentDraftId = draft.id
                currentDraftAudioPath = draft.audioPath  // <-- track audio path
                currentDraftVideoPath = draft.videoPath // <-- track video path
                currentDraftSymptoms = draft.symptoms // <-- track symptoms

                recordingProgress = RecordingProgress(
                    heartRateCompleted = draft.videoPath != null,
                    respiratoryCompleted = draft.audioPath != null,
                    symptomsCompleted = draft.symptoms.isNotEmpty()
                )
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val history = repository.getAllHistory()
            savedSessions = history.map { recording ->
                com.example.contextmonitoring.data.RecordingSession(
                    id = recording.id,
                    createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(recording.savedAt), java.time.ZoneId.systemDefault()),
                    heartRateVideoPath = recording.videoPath,
                    respiratoryAudioPath = recording.audioPath,
                    symptoms = recording.symptoms
                )
            }
        }
    }

    // Add mock data for testing
    init {
        // This will be replaced with real database fetch
        loadCurrentDraft()
        loadHistory()
    }

    fun markHeartRateCompleted(videoPath: String) {
        viewModelScope.launch {
            if (currentDraftId != null) {
                // Update existing draft
                repository.updateDraftRecording(
                    draftId = currentDraftId!!,
                    videoPath = videoPath
                )
            } else {
                // Create new draft
                repository.saveDraftRecording(videoPath, null, emptyList())
            }
            recordingProgress = recordingProgress.copy(heartRateCompleted = true)
        }
    }

    fun markRespiratoryCompleted(audioPath:String){
        viewModelScope.launch {
            if (currentDraftId != null) {
                // Update existing draft
                repository.updateDraftRecording(
                    draftId = currentDraftId!!,
                    audioPath = audioPath
                )
            } else {
                // Create new draft
                repository.saveDraftRecording(null, audioPath, emptyList())
            }
            recordingProgress = recordingProgress.copy(respiratoryCompleted = true)
        }
    }

    fun markSymptomsCompleted(selectedSymptomIds: List<String>){
        viewModelScope.launch {
            if(currentDraftId!=null){
                repository.updateDraftRecording(
                    draftId = currentDraftId!!,
                    symptoms = selectedSymptomIds
                )
            } else {
                // Create new draft
                repository.saveDraftRecording(null,null , selectedSymptomIds)
            }
            recordingProgress = recordingProgress.copy(symptomsCompleted = true)
        }
    }

    fun unmarkHeartRate() {
        viewModelScope.launch {
            if (currentDraftId != null) {
                repository.clearVideoPath(
                    draftId = currentDraftId!!
                )
            }
            recordingProgress = recordingProgress.copy(heartRateCompleted = false)
        }
    }

    fun unmarkRespiratory() {
        viewModelScope.launch {
            if (currentDraftId != null) {
                repository.clearAudioPath(
                    draftId = currentDraftId!!
                )
            }
            recordingProgress = recordingProgress.copy(respiratoryCompleted = false)
        }
    }


    fun saveCurrentSession() {
        viewModelScope.launch {
            currentDraftId?.let { draftId ->
                val historyId = repository.moveDraftToHistory(draftId)
                println("History ID after move $historyId")
                if (historyId != -1L) {
                    // Success - reset progress and reload data
                    resetProgress()
                    loadHistory()
                }
            }
        }
    }

    fun resetProgress() {
        recordingProgress = RecordingProgress()
        currentDraftId = null
    }

    fun deleteAllRecords() {
        viewModelScope.launch {
            repository.deleteAllRecords()
            resetProgress()
            loadHistory()
        }
    }
}
----
without writing code, tell me about the relation between savedSessions record history card and viewmodel data