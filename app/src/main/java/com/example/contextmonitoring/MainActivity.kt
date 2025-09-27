package com.example.contextmonitoring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contextmonitoring.ui.screens.AudioRecordingScreen
import com.example.contextmonitoring.ui.screens.HealthMonitorScreen
import com.example.contextmonitoring.ui.screens.HomeScreen
import com.example.contextmonitoring.ui.screens.VideoRecordingScreen
import com.example.contextmonitoring.ui.theme.ContextMonitoringTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContextMonitoringTheme {
                val navController = rememberNavController()

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
                            }
                        )
                    }
                    composable("video_recording") {
                        VideoRecordingScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onRecordingComplete = { videoPath ->
                                // TODO: Save to database later
                                println("Video recording complete at path $videoPath")
                            }
                        )
                    }
                    composable("audio_recording") {
                        AudioRecordingScreen(
                            onBackClick = { navController.popBackStack() },
                            onRecordingComplete = { audioPath ->
                                // TODO: Save to database later
                                println("Audio recording complete at path $audioPath")
                            }
                        )
                    }
                }
            }
        }
    }
}