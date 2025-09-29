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
        println("attempting to save $videoPath")
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