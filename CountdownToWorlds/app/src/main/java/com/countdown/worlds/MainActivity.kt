package com.countdown.worlds

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Trigger an immediate widget update
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widget = ComponentName(this, CountdownWidget::class.java)
        val ids = appWidgetManager.getAppWidgetIds(widget)
        CountdownWidget().onUpdate(this, appWidgetManager, ids)
    }
}
