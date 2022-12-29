package com.example.exampleworkmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class DownloadWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        try {
            val count = inputData.getInt(MainActivity.KEY_COUNT_VALUE, 0)

            for (i in 0 until count) {
                Log.d("WorkManager", "Downloading $i")
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}