package com.example.contextmonitoring.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.contextmonitoring.utils.CameraXVideoRecorder

@Composable
fun CameraXPreview(
    cameraRecorder: CameraXVideoRecorder,
    modifier: Modifier = Modifier,
    onRecordingComplete: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { androidx.camera.view.PreviewView(context) }

    LaunchedEffect(Unit) {
        cameraRecorder.startCamera(previewView)
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

    // Button row for recording
    var isRecording by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            if (!isRecording) {
                cameraRecorder.startRecording { uri ->
                    isRecording = false
                    onRecordingComplete(uri)
                }
                isRecording = true
            } else {
                cameraRecorder.stopRecording()
                isRecording = false
            }
        }) {
            Text(if (isRecording) "Stop" else "Record")
        }
    }
}
