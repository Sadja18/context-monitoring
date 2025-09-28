package com.example.contextmonitoring.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class Media3Helper private constructor() {
private var exoPlayer: ExoPlayer? = null

    fun createPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
        return exoPlayer!!
    }

    fun playMedia(filePath: String, onCompletion: (() -> Unit)? = null) {
        val player = exoPlayer ?: return

        // Set completion listener
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == androidx.media3.common.Player.STATE_ENDED) {
                    onCompletion?.invoke()
                }
            }
        })

        // Prepare and play media
        val mediaItem = MediaItem.fromUri(filePath)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun stop() {
        exoPlayer?.let {
            it.stop()
            it.release()
        }
        exoPlayer = null
    }

    companion object {
        @Volatile
        private var INSTANCE: Media3Helper? = null

        fun getInstance(): Media3Helper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Media3Helper().also { INSTANCE = it }
            }
        }
    }
}

@Composable
fun rememberMedia3Helper(): Media3Helper {
val context = LocalContext.current
val media3Helper = remember { Media3Helper.getInstance() }

    DisposableEffect(context) {
        onDispose {
            media3Helper.stop()
        }
    }

    return media3Helper
}
----
package com.example.contextmonitoring.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.ui.BuildConfig
import com.example.contextmonitoring.utils.FileUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoRecordingScreen(
onBackClick: () -> Unit,
onRecordingComplete: (String) -> Unit,
modifier: Modifier = Modifier
) {
val context = LocalContext.current
var recordedVideoPath by remember { mutableStateOf<String?>(null) }
var isRecording by remember { mutableStateOf(false) }
var currentTime by remember { mutableStateOf(0) }
val maxDuration = if(BuildConfig.DEBUG) 5 else 45 // demo seconds
var isPlaying by remember { mutableStateOf(false) }
val mediaPlayer = remember { MediaPlayer() }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            currentTime = 0
            while (currentTime < maxDuration) {
                delay(1000)
                currentTime++
            }
            isRecording = false
            // Save demo audio file
            val videoFile = FileUtils.createVideoFile(context)
            recordedVideoPath = videoFile.absolutePath
            onRecordingComplete(recordedVideoPath!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Recording", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Camera preview

            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    if (!isRecording) {
                        val videoFile = FileUtils.createVideoFile(context)
                        recordedVideoPath = videoFile.absolutePath


                        isRecording = true
                    } else {

                        isRecording = false
                    }
                }) {
                    Text(if (isRecording) "Stop" else "Record")
                }

                if (recordedVideoPath != null) {
                    Button(onClick = {
                        if (!isPlaying) {

                            isPlaying = true
                        } else {

                            isPlaying = false
                        }
                    }) {
                        Text(if (isPlaying) "Pause" else "Play")
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
        }
    }
}
---
I want to add record an actual video and play it back using camera-x