package com.example.exampleworkmanager

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

class UploadWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    companion object {
        const val KEY_DATE_FINISHED_VALUE = "key_date_finished_value"
    }
    override fun doWork(): Result {
        try {
            val count = inputData.getInt(MainActivity.KEY_COUNT_VALUE, 0)

            for (i in 0 until count) {
                Log.d("WorkManager", "Uploading $i")
            }

            val dateFormat = SimpleDateFormat("dd.mm.yyyy hh:mm:ss", Locale("pl_PL"))
            val dateFinished = dateFormat.format(Date())
            val outputData = Data.Builder()
                .putString(KEY_DATE_FINISHED_VALUE, dateFinished)
                .build()

            return Result.success(outputData)
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}