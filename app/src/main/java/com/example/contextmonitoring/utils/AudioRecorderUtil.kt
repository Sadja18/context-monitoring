package com.example.contextmonitoring.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException

class AudioRecorderUtil(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var onComplete: ((String) -> Unit)? = null

    fun startRecording(file: File, onComplete: (String) -> Unit) {
        if (!hasAudioPermission()) {
            Log.e("AudioRecorderUtil", "Missing RECORD_AUDIO permission")
            return
        }

        this.outputFile = file
        this.onComplete = onComplete

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        try {
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
                Log.d("AudioRecorderUtil", "Recording started: ${file.absolutePath}")
            }
        } catch (e: IOException) {
            Log.e("AudioRecorderUtil", "startRecording failed", e)
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null
            outputFile?.let {
                onComplete?.invoke(it.absolutePath)
            }
            Log.d("AudioRecorderUtil", "Recording stopped: ${outputFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("AudioRecorderUtil", "stopRecording failed", e)
        }
    }

    fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestAudioPermission(activity: android.app.Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            2001
        )
    }
}
