package com.example.contextmonitoring.data

data class SymptomOption(
    val id: String,
    val label: String,
    val category: String = "General" // Optional: for grouping later
)

object SymptomsData {
    // MODIFY THIS LIST TO CHANGE SYMPTOMS
    val symptomOptions = listOf(
        SymptomOption("fever", "Fever"),
        SymptomOption("cough", "Cough"),
        SymptomOption("shortness_breath", "Shortness of breath"),
        SymptomOption("fatigue", "Fatigue"),
        SymptomOption("headache", "Headache"),
        SymptomOption("sore_throat", "Sore throat"),
        SymptomOption("runny_nose", "Runny nose"),
        SymptomOption("nausea", "Nausea"),
        SymptomOption("diarrhea", "Diarrhea"),
        SymptomOption("muscle_pain", "Muscle pain"),
        SymptomOption("chills", "Chills"),
        SymptomOption("loss_taste", "Loss of taste/smell")
        // Add more options here as needed
    )
}