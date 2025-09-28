package com.example.contextmonitoring.utils

import android.content.Context
import android.os.Environment
import java.io.File

object FileUtils {
    fun createVideoFile(context: Context): File {
        val timeStamp = System.currentTimeMillis()
        val videoFileName = "VID_${timeStamp}.mp4"
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            videoFileName
        )
    }

    fun createAudioFile(context: Context): File {
        val timeStamp = System.currentTimeMillis()
        val audioFileName = "AUD_${timeStamp}.mp3"
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            audioFileName
        )
    }

    fun getVideoFilePath(context: Context, fileName: String): String {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            fileName
        ).absolutePath
    }

    fun getAudioFilePath(context: Context, fileName: String): String {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            fileName
        ).absolutePath
    }
}