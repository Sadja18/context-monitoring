package com.example.contextmonitoring.data

import java.time.LocalDateTime

data class RecordingSession(
    val id: Long,
    val createdAt: LocalDateTime,
    val heartRateVideoPath: String?,
    val respiratoryAudioPath: String?,
    val symptoms: List<String> // List of symptom IDs
) {
    // Helper to get symptom labels for display
    fun getSymptomLabels(): String {
        return symptoms.joinToString(", ") { symptomId ->
            SymptomsData.symptomOptions
                .find { it.id == symptomId }
                ?.label ?: symptomId
        }
    }

    // Format date for display
    fun getFormattedDate(): String {
        return createdAt.toString().substring(0, 16) // "yyyy-MM-dd HH:mm"
    }
}