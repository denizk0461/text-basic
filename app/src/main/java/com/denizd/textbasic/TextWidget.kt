package com.denizd.textbasic

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.widget.RemoteViews

class TextWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val remoteViews = configureViews(context, appWidgetIds)
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun configureViews(context: Context, appWidgetIds: IntArray): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_text).apply {

            val storage = QuoteStorage(context)

            val quote = if (storage.isOrderRandom()) {
                storage.getRandomQuote()
            } else {
                storage.getNextQuote()
            }

            setTextViewText(R.id.widget_text, quote.ifBlank { context.getString(R.string.info_add_text) })
            setTextViewTextSize(R.id.widget_text, TypedValue.COMPLEX_UNIT_SP, storage.getTextSize().toFloat())

            val colours = storage.getColours(context = context)

            setTextColor(R.id.widget_text, colours.first)
            setInt(R.id.background, "setBackgroundColor", colours.second)

            val gravity = when (storage.getWidgetGravity()) {
                0 -> Gravity.START or Gravity.TOP
                1 -> Gravity.CENTER_HORIZONTAL or Gravity.TOP
                2 -> Gravity.END or Gravity.TOP
                3 -> Gravity.START or Gravity.CENTER_VERTICAL
                4 -> Gravity.CENTER
                5 -> Gravity.END or Gravity.CENTER_VERTICAL
                6 -> Gravity.START or Gravity.BOTTOM
                7 -> Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                8 -> Gravity.END or Gravity.BOTTOM
                else -> Gravity.CENTER
            }

            setInt(R.id.root_layout, "setGravity", gravity)

            setOnClickPendingIntent(
                R.id.root_layout,
                Intent(context, TextWidget::class.java).let { intent ->
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                    PendingIntent.getBroadcast(
                        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                    )
                }
            )
        }
}