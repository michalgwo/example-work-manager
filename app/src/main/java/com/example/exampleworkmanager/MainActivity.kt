package com.example.exampleworkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var bTask1: Button
    private lateinit var bTask2: Button
    private lateinit var tvTaskStatus1: TextView
    private lateinit var tvTaskStatus2: TextView
    private lateinit var tvTaskStatus3: TextView
    private lateinit var tvTaskStatus4: TextView

    companion object {
        const val KEY_COUNT_VALUE = "key_count_value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bTask1 = findViewById(R.id.bTask1)
        bTask2 = findViewById(R.id.bTask2)
        tvTaskStatus1 = findViewById(R.id.tvTaskStatus1)
        tvTaskStatus2 = findViewById(R.id.tvTaskStatus2)
        tvTaskStatus3 = findViewById(R.id.tvTaskStatus3)
        tvTaskStatus4 = findViewById(R.id.tvTaskStatus4)

        bTask1.setOnClickListener {
            oneTimeWorkRequest()
        }

        bTask2.setOnClickListener {
            periodicWorkRequest()
        }
    }

    private fun periodicWorkRequest() {
        val workManager = WorkManager.getInstance(applicationContext)

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputDataDownload = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 190000)
            .build()

        val downloadRequest = PeriodicWorkRequest.Builder(UploadWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(inputDataDownload)
            .build()

        workManager.enqueue(downloadRequest)

        workManager.getWorkInfoByIdLiveData(downloadRequest.id).observe(this) {
            tvTaskStatus4.text = "Download ${it.state.name}"
        }
    }

    private fun oneTimeWorkRequest() {
        val workManager = WorkManager.getInstance(applicationContext)

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputDataUpload = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 100000)
            .build()

        val filterRequest = OneTimeWorkRequest.Builder(FilterWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputDataUpload)
            .build()

        val compressRequest = OneTimeWorkRequest.Builder(CompressWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputDataUpload)
            .build()

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputDataUpload)
            .build()

        workManager
            .beginWith(filterRequest)
            .then(compressRequest)
            .then(uploadRequest)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(filterRequest.id).observe(this) {
            tvTaskStatus1.text = "Filter ${it.state.name}"
        }

        workManager.getWorkInfoByIdLiveData(compressRequest.id).observe(this) {
            tvTaskStatus2.text = "Compress ${it.state.name}"
        }

        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this) {
            tvTaskStatus3.text = "Upload ${it.state.name}"
            if (it.state.isFinished) {
                val finishedDate = it.outputData.getString(UploadWorker.KEY_DATE_FINISHED_VALUE)
                tvTaskStatus3.append("\nCOMPLETION DATE: $finishedDate")
            }
        }
    }
}