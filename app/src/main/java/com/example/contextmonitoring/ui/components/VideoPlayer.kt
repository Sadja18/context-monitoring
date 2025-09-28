package com.example.contextmonitoring.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoPlayer(videoUri: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            setMediaItem(androidx.media3.common.MediaItem.fromUri(android.net.Uri.parse(videoUri)))
            prepare()
            play()
        }
    }

    DisposableEffect(videoUri) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { ctx ->
            androidx.media3.ui.PlayerView(ctx).apply { player = exoPlayer }
        },
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}
