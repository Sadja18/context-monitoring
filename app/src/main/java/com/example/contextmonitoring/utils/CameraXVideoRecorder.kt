package com.example.contextmonitoring.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXVideoRecorder(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var camera: androidx.camera.core.Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var recording: Recording? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var onRecordingComplete: ((String) -> Unit)? = null

    fun startCamera(previewView: androidx.camera.view.PreviewView) {
        if (!hasCameraPermission()) {
            requestCameraPermission()
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindPreview(previewView)
            } catch (e: Exception) {
                Log.e("CameraXVideoRecorder", "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindPreview(previewView: androidx.camera.view.PreviewView) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val recorder = Recorder.Builder()
            .setQualitySelector(androidx.camera.video.QualitySelector.from(
                androidx.camera.video.Quality.FHD
            ))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        cameraProvider?.unbindAll()
        try {
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
                videoCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraXVideoRecorder", "Use case binding failed", exc)
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording(onComplete: (String) -> Unit) {
        if (!hasCameraPermission()) {
            onComplete("")
            return
        }

        val videoCapture = videoCapture ?: return

        val name = "VID_${SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(Date())}.mp4"
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, name)
            put(android.provider.MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(android.provider.MediaStore.Video.Media.RELATIVE_PATH, "Movies/ContextMonitoring")
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        recording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> Log.d("CameraXVideoRecorder", "Recording started")
                    is VideoRecordEvent.Finalize -> {
                        Log.d("CameraXVideoRecorder", "Recording ended")
                        onComplete(event.outputResults.outputUri.toString())
                    }
                    is VideoRecordEvent.Status -> { /* optional: handle duration/size updates */}
                }
            }
    }


    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    fun release() {
        executor.shutdown()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val activity = context as? androidx.fragment.app.FragmentActivity ?: return
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            1001
        )
    }
}