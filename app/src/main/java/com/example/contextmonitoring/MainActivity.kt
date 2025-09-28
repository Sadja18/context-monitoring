package com.example.contextmonitoring

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contextmonitoring.ui.screens.AudioRecordingScreen
import com.example.contextmonitoring.ui.screens.HealthMonitorScreen
import com.example.contextmonitoring.ui.screens.HomeScreen
import com.example.contextmonitoring.ui.screens.SymptomsSelectionScreen
import com.example.contextmonitoring.ui.screens.VideoRecordingScreen
import com.example.contextmonitoring.ui.theme.ContextMonitoringTheme

import com.example.contextmonitoring.viewmodel.HealthMonitorViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContextMonitoringTheme {
                val navController = rememberNavController()
                val healthViewModel: HealthMonitorViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application as Application)
                )

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onVitalCaptureClick = {
                                print("Navigating to health monitor")
                                navController.navigate("health_monitor")
                            }
                        )
                    }
                    composable("health_monitor") {
                        HealthMonitorScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onHeartRateClick = {
                                navController.navigate("video_recording")
                            },
                            onRespiratoryClick = {
                                navController.navigate("audio_recording")
                            },
                            onSymptomsClick = { navController.navigate("symptoms_selection") },
                            recordingProgress = healthViewModel.recordingProgress,
                            savedSessions = healthViewModel.savedSessions // pass saved sessions

                        )
                    }
                    composable("video_recording") {
                        VideoRecordingScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onRecordingComplete = { videoPath ->
                                // TODO: Save to database later
                                healthViewModel.markHeartRateCompleted(videoPath)
                                println("Video recording complete at path $videoPath")
                            }
                        )
                    }
                    composable("audio_recording") {
                        AudioRecordingScreen(
                            onBackClick = { navController.popBackStack() },
                            onRecordingComplete = { audioPath ->
                                // TODO: Save to database later
                                healthViewModel.markRespiratoryCompleted(audioPath)
                                println("Audio recording complete at path $audioPath")
                            }
                        )
                    }
                    composable("symptoms_selection") {
                        SymptomsSelectionScreen(
                            onBackClick = { navController.popBackStack() },
                            onSaveSymptoms = { selectedSymptomIds ->
                                // TODO: Save to database later
                                healthViewModel.markSymptomsCompleted(selectedSymptomIds)
                                println("Symptoms selected are $selectedSymptomIds")
                                //  navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}