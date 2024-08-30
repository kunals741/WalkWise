package com.kunal.walkwise.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.kunal.walkwise.Constants
import com.kunal.walkwise.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StepTrackingService : Service() {

    private var count = 1

    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        // get the steps here from sensores
        job = CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                println("count : $count")
                delay(2000)
                count++
            }
        }
        startForeground(Constants.STEP_TRACKING_FOREGROUND_SERVICE_CODE, notification)
        return START_STICKY
    }


    private fun createNotification(): Notification {
        val builder =
            NotificationCompat.Builder(this, Constants.STEP_TRACKING_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("WalkWise is tracking steps")
                .setContentText("WalkWise tracking is running in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        return builder
    }


    //todo can be shifted to app application class:
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Step Tracking"
            val channelID = Constants.STEP_TRACKING_NOTIFICATION_CHANNEL_ID
            val channelImportance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(channelID, channelName, channelImportance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun saveDataInRoomDB(stepsData : String){
        //todo save in room db
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}