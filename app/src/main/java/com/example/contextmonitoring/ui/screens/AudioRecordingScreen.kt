package com.example.contextmonitoring.ui.screens

//import androidx.media3.ui.BuildConfig
import android.media.MediaPlayer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.contextmonitoring.BuildConfig
import com.example.contextmonitoring.ui.components.AudioRecordingCard
import com.example.contextmonitoring.utils.AudioRecorderUtil
import com.example.contextmonitoring.utils.FileUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    existingAudioPath: String? = null,
    onBackClick: () -> Unit,
    onRecordingComplete: (String) -> Unit,
    onDelete: ()->Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioPath by remember { mutableStateOf<String?>(existingAudioPath) }
    var currentTime by remember { mutableStateOf(0) }
    val maxDuration = if (BuildConfig.DEBUG) 5 else 45 // demo seconds

    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }
    val audioRecorder = remember { AudioRecorderUtil(context) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            val audioFile = FileUtils.createAudioFile(context)

            audioRecorder.startRecording(audioFile) { path ->
                recordedAudioPath = path
                onRecordingComplete(path)
            }

            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }

            // stop after maxDuration
            audioRecorder.stopRecording()
            isRecording = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Audio Recording",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        AudioRecordingCard(
            isRecording = isRecording,
            isPlaying = isPlaying,
            recordedAudioPath = recordedAudioPath,
            currentTime = currentTime,
            maxDuration = maxDuration,
            onStartRecording = {
                println("recording started audio ")
                isRecording = true
            },
            onPlaybackClick = {
                if (!isPlaying && recordedAudioPath != null) {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(recordedAudioPath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    isPlaying = true
                    mediaPlayer.setOnCompletionListener { isPlaying = false }
                } else {
                    mediaPlayer.pause()
                    isPlaying = false
                }
            },
            onDeleteClick = {
                if (isPlaying) {
                    mediaPlayer.stop()
                    isPlaying = false
                }

                recordedAudioPath = null
                currentTime = 0
                onDelete()
            },
            modifier = Modifier.padding(padding)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}
