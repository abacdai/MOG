package com.example.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import java.util.Calendar

object DeviceHealthTracker {

    fun hasUsagePermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    /**
     * Calculates total screen time (foreground app time) in hours for today (since midnight).
     */
    fun getTodayScreenTimeHours(context: Context): Float {
        if (!hasUsagePermission(context)) return 0f
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return 0f

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val now = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDay,
            now
        ) ?: return 0f

        var totalMillis = 0L
        for (stat in stats) {
            // Filter system idle processes
            if (stat.packageName != "android" && stat.packageName != "com.android.systemui") {
                totalMillis += stat.totalTimeInForeground
            }
        }

        val hours = totalMillis.toFloat() / (1000f * 60f * 60f)
        return Math.round(hours * 10f) / 10f
    }

    /**
     * Estimates sleep time (in hours) based on night phone inactivity (between 9:00 PM yesterday and 9:00 AM today).
     */
    fun getEstimatedSleepHours(context: Context): Float {
        if (!hasUsagePermission(context)) return 0f
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return 0f

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 21) // 9:00 PM yesterday
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nightStart = calendar.timeInMillis

        val morningCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9) // 9:00 AM today
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nightEnd = minOf(morningCalendar.timeInMillis, System.currentTimeMillis())

        if (nightEnd <= nightStart) return 0f

        try {
            val events = usageStatsManager.queryEvents(nightStart, nightEnd)
            var lastActiveTime = nightStart
            var maxInactivityWindow = 0L
            val event = UsageEvents.Event()

            var hasAnyNightEvent = false
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                // App opened or screen turned on
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                    event.eventType == 15 /* SCREEN_INTERACTIVE */
                ) {
                    hasAnyNightEvent = true
                    val inactiveDuration = event.timeStamp - lastActiveTime
                    if (inactiveDuration > maxInactivityWindow) {
                        maxInactivityWindow = inactiveDuration
                    }
                    lastActiveTime = event.timeStamp
                }
            }

            // Check trailing inactivity from last active time until wake up window
            val trailingInactive = nightEnd - lastActiveTime
            if (trailingInactive > maxInactivityWindow) {
                maxInactivityWindow = trailingInactive
            }

            // If phone wasn't touched all night or had a long gap of at least 3 hours
            val sleepHours = maxInactivityWindow.toFloat() / (1000f * 60f * 60f)
            if (sleepHours >= 3.0f) {
                return minOf(Math.round(sleepHours * 10f) / 10f, 12.0f)
            }
        } catch (_: Exception) {
            // Fallback
        }

        return 0f
    }
}
