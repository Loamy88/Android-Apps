package com.countdown.worlds

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.Calendar
import java.util.concurrent.TimeUnit

class CountdownWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
        context.startForegroundService(Intent(context, UpdateService::class.java))
    }

    override fun onEnabled(context: Context) {
        context.startForegroundService(Intent(context, UpdateService::class.java))
    }

    override fun onDisabled(context: Context) {
        context.stopService(Intent(context, UpdateService::class.java))
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TOGGLE) {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val paused = prefs.getBoolean("paused", false)
            prefs.edit().putBoolean("paused", !paused).apply()

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, CountdownWidget::class.java)
            )
            for (id in ids) updateAppWidget(context, manager, id)
        }
        super.onReceive(context, intent)
    }

    companion object {
        const val ACTION_TOGGLE = "com.countdown.worlds.ACTION_TOGGLE"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val paused = prefs.getBoolean("paused", false)

            val views = RemoteViews(context.packageName, R.layout.widget_countdown)

            // Toggle click intent
            val toggleIntent = Intent(context, CountdownWidget::class.java).apply {
                action = ACTION_TOGGLE
            }
            val togglePending = PendingIntent.getBroadcast(
                context, 0, toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, togglePending)

            // Pause/play icon
            views.setTextViewText(R.id.tv_toggle_icon, if (paused) "▶" else "⏸")

            if (!paused) {
                val countdown = getCountdown()
                if (countdown.days == 0L && countdown.hours == 0 && countdown.minutes == 0 && countdown.seconds == 0) {
                    views.setTextViewText(R.id.tv_days, "🏆")
                    views.setTextViewText(R.id.tv_days_label, "IT'S TIME!")
                    views.setTextViewText(R.id.tv_hours, "")
                    views.setTextViewText(R.id.tv_minutes, "")
                    views.setTextViewText(R.id.tv_seconds, "")
                } else {
                    views.setTextViewText(R.id.tv_days, countdown.days.toString())
                    views.setTextViewText(R.id.tv_hours, String.format("%02d", countdown.hours))
                    views.setTextViewText(R.id.tv_minutes, String.format("%02d", countdown.minutes))
                    views.setTextViewText(R.id.tv_seconds, String.format("%02d", countdown.seconds))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getCountdown(): CountdownData {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.APRIL)
                set(Calendar.DAY_OF_MONTH, 28)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) add(Calendar.YEAR, 1)
            }
            val diffMs = maxOf(0L, target.timeInMillis - now.timeInMillis)
            return CountdownData(
                TimeUnit.MILLISECONDS.toDays(diffMs),
                (TimeUnit.MILLISECONDS.toHours(diffMs) % 24).toInt(),
                (TimeUnit.MILLISECONDS.toMinutes(diffMs) % 60).toInt(),
                (TimeUnit.MILLISECONDS.toSeconds(diffMs) % 60).toInt()
            )
        }
    }

    data class CountdownData(val days: Long, val hours: Int, val minutes: Int, val seconds: Int)
}
