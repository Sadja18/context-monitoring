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