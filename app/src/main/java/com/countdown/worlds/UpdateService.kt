package com.countdown.worlds

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class UpdateService : Service() {

    private var running = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())

        if (!running) {
            running = true
            Thread {
                while (running) {
                    val paused = getSharedPreferences("widget_prefs", MODE_PRIVATE)
                        .getBoolean("paused", false)

                    if (!paused) {
                        val manager = AppWidgetManager.getInstance(this)
                        val ids = manager.getAppWidgetIds(
                            ComponentName(this, CountdownWidget::class.java)
                        )
                        for (id in ids) {
                            CountdownWidget.updateAppWidget(this, manager, id)
                        }
                    }

                    try { Thread.sleep(1000) } catch (e: InterruptedException) { break }
                }
            }.start()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val channelId = "countdown_channel"
        val channel = NotificationChannel(
            channelId, "Countdown Widget", NotificationManager.IMPORTANCE_MIN
        ).apply { setShowBadge(false) }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Countdown to Worlds is running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()
    }
}
