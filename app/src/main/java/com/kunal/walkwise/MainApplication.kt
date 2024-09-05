package com.kunal.walkwise

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MainRepository.initialize(this)
        createNotificationChannel()
    }


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
}