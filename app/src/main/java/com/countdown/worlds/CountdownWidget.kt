package com.countdown.worlds

import android.app.AlarmManager
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
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        scheduleNextUpdate(context)
    }

    override fun onEnabled(context: Context) {
        scheduleNextUpdate(context)
    }

    override fun onDisabled(context: Context) {
        cancelUpdates(context)
    }

    companion object {
        const val ACTION_UPDATE = "com.countdown.worlds.UPDATE_WIDGET"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_countdown)
            val countdown = getCountdown()

            views.setTextViewText(R.id.tv_days, countdown.days.toString())
            views.setTextViewText(R.id.tv_hours, String.format("%02d", countdown.hours))
            views.setTextViewText(R.id.tv_minutes, String.format("%02d", countdown.minutes))
            views.setTextViewText(R.id.tv_seconds, String.format("%02d", countdown.seconds))

            if (countdown.days == 0L && countdown.hours == 0 && countdown.minutes == 0 && countdown.seconds == 0) {
                views.setTextViewText(R.id.tv_days, "🏆")
                views.setTextViewText(R.id.tv_days_label, "IT'S TIME!")
                views.setTextViewText(R.id.tv_hours, "")
                views.setTextViewText(R.id.tv_minutes, "")
                views.setTextViewText(R.id.tv_seconds, "")
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
                // If April 28 has already passed this year, use next year
                if (before(now)) {
                    add(Calendar.YEAR, 1)
                }
            }

            val diffMs = maxOf(0L, target.timeInMillis - now.timeInMillis)

            val days = TimeUnit.MILLISECONDS.toDays(diffMs)
            val hours = (TimeUnit.MILLISECONDS.toHours(diffMs) % 24).toInt()
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(diffMs) % 60).toInt()
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(diffMs) % 60).toInt()

            return CountdownData(days, hours, minutes, seconds)
        }

        private fun scheduleNextUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, CountdownWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            // Update every minute
            val nextMinute = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            alarmManager.setRepeating(
                AlarmManager.RTC,
                nextMinute.timeInMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent
            )
        }

        private fun cancelUpdates(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, CountdownWidget::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    data class CountdownData(val days: Long, val hours: Int, val minutes: Int, val seconds: Int)
}
