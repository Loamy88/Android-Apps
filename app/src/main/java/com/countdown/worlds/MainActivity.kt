package com.countdown.worlds

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Starting from an Activity is always allowed on Android 12+
        startForegroundService(Intent(this, UpdateService::class.java))

        // Also trigger an immediate widget update
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(this, CountdownWidget::class.java)
        )
        for (id in ids) CountdownWidget.updateAppWidget(this, appWidgetManager, id)
    }

    override fun onResume() {
        super.onResume()
        // Re-start service in case it was killed while app was in background
        startForegroundService(Intent(this, UpdateService::class.java))
    }
}

