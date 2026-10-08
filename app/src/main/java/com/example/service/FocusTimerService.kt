package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class FocusTimerService : Service() {

    companion object {
        const val CHANNEL_ID = "focus_timer_channel"
        const val NOTIFICATION_ID = 101

        const val ACTION_START = "ACTION_START"
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_SECONDS_LEFT = "EXTRA_SECONDS_LEFT"
        const val EXTRA_IS_COUNT_UP = "EXTRA_IS_COUNT_UP"

        fun startService(context: Context, remainingSeconds: Int, isCountUp: Boolean = false) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_SECONDS_LEFT, remainingSeconds)
                putExtra(EXTRA_IS_COUNT_UP, isCountUp)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateNotification(context: Context, remainingSeconds: Int, isCountUp: Boolean = false) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_SECONDS_LEFT, remainingSeconds)
                putExtra(EXTRA_IS_COUNT_UP, isCountUp)
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_START, ACTION_UPDATE -> {
                val seconds = intent.getIntExtra(EXTRA_SECONDS_LEFT, 0)
                val isCountUp = intent.getBooleanExtra(EXTRA_IS_COUNT_UP, false)
                val notification = buildNotification(seconds, isCountUp)
                startForeground(NOTIFICATION_ID, notification)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(seconds: Int, isCountUp: Boolean): Notification {
        val m = seconds / 60
        val s = seconds % 60
        val timeFormatted = String.format("%02d:%02d", m, s)
        val title = if (isCountUp) "Đang tập trung tự do ✦" else "Thời gian tập trung còn lại"

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("⏱ $timeFormatted - Giữ vững sự tập trung nhé!")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bộ đếm giờ tập trung",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hiển thị đếm ngược thời gian tập trung trong nền"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
