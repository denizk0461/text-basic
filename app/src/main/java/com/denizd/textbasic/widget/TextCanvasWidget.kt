package com.denizd.textbasic.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.denizd.textbasic.R
import kotlin.random.Random

class TextCanvasWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->

            val remoteViews = configureViews(context, appWidgetIds)
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun configureViews(context: Context, appWidgetIds: IntArray, size: Pair<Int, Int>? = null): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_bitmap).apply {

            val bitmap = CanvasText.drawText(context, size = size)

            setImageViewBitmap(R.id.bitmap_view, bitmap)

            setOnClickPendingIntent(
                R.id.bmp_root_layout,
                Intent(context, TextCanvasWidget::class.java).let { intent ->
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                    PendingIntent.getBroadcast(
                        context, Random.nextInt(), intent, PendingIntent.FLAG_IMMUTABLE
                    )
                }
            )
        }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        val width = (newOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH) ?: 0)
        val height = (newOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT) ?: 0)

//        Log.d("ASDFF", (newOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)).toString())


        context?.let {
            val remoteViews = configureViews(context, intArrayOf(appWidgetId), Pair(width, height))
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
        }
    }
}