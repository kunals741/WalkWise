package com.kunal.walkwise.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kunal.walkwise.Constants
import com.kunal.walkwise.MainRepository
import com.kunal.walkwise.R
import com.kunal.walkwise.database.StepsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StepTrackingService : Service(), SensorEventListener {



    private var currentSteps = 0L
    private var updateJob: Job? = null
    private val mainRepository = MainRepository.get()
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepSensor == null) {
            Log.e(TAG, "Step counter sensor not available on this device")
        } else {
            Log.d(TAG, "Step counter sensor found: ${stepSensor?.name}")
        }

        serviceScope.launch {
            currentSteps = mainRepository.getCurrentStepsFromUserId("2")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(Constants.STEP_TRACKING_FOREGROUND_SERVICE_CODE, notification)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        startStepUpdates()

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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }

    private fun startStepUpdates() {
        updateJob = serviceScope.launch {
            while (isActive) {
                val steps = currentSteps
                Log.d(TAG, "Update cycle - Current steps: $steps")
                mainRepository.updateStepsForUser(StepsData("2", steps))
                delay(UPDATE_INTERVAL)
            }
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            val steps = currentSteps++
            Log.d(TAG, "Step Detector event - Total steps: $steps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //do nothing
    }

    companion object {
        private const val TAG = "StepTrackingService"
        private const val UPDATE_INTERVAL = 1000L // 1 second
    }
}