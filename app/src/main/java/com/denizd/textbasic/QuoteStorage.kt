package com.denizd.textbasic

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.preference.PreferenceManager

class QuoteStorage(context: Context) {

    companion object {

        const val PREF_NAME = "textbasicprefs"
        const val PREF_BACKUP_KEY = "textbasicprefskey"
        const val SEPARATOR = '\uFFFF'

        private const val KEY_QUOTES = "quotes"
        private const val KEY_QUOTE_COUNTER = "quotecounter"
        private const val KEY_TEXT_SIZE = "textsize"
        private const val KEY_INVERTED = "inverted"
        private const val KEY_HIGH_CONTRAST = "hicontrast"
        private const val KEY_TRANSPARENCY = "transparency"
        private const val KEY_RANDOM = "random"
        private const val KEY_WIDGET_POSITION = "widgetposition"
        private const val KEY_BACKGROUND_TYPE = "backgroundtype"
        private const val KEY_TYPEFACE = "typeface"
        private const val KEY_TYPEFACE_STYLE = "typefacestyle"
        private const val KEY_AVG_WIDTH = "avgwidth"
        private const val KEY_AVG_HEIGHT = "avgheight"
        private const val KEY_OUTLINE_SIZE = "outlinesize"

        val prefGroups = arrayOf(
            KEY_QUOTES, KEY_QUOTE_COUNTER, KEY_TEXT_SIZE, KEY_INVERTED, KEY_HIGH_CONTRAST,
            KEY_TRANSPARENCY, KEY_RANDOM, KEY_WIDGET_POSITION
        )


//        val transparencyValues = arrayOf(
//            "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50",
//            "55", "60", "65", "70", "75", "80", "85", "90", "95", "100",
//        )
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, 0)

    fun getAllQuotes(): List<String> = ((prefs.getString(KEY_QUOTES, "")) ?: "").split(SEPARATOR).toList()

    fun getRandomQuote(): String {
        val quotes = getAllQuotes()
        return quotes.random()
    }

    fun getNextQuote(): String {
        val currentQuoteCounter: Int = prefs.getInt(KEY_QUOTE_COUNTER, 0)
        val quotes = getAllQuotes()
        val nextQuoteCounter: Int = if (currentQuoteCounter >= quotes.size - 1) 0 else currentQuoteCounter + 1
        prefs.edit().putInt(
            KEY_QUOTE_COUNTER,
            nextQuoteCounter
        ).apply()
        return quotes[nextQuoteCounter]
    }

    fun getTextSize() = prefs.getInt(KEY_TEXT_SIZE, 18)

    fun isInvertedEnabled(): Boolean = prefs.getBoolean(KEY_INVERTED, false)
    fun isHighContrastEnabled(): Boolean = prefs.getBoolean(KEY_HIGH_CONTRAST, false)
    fun getTransparency(): Int = prefs.getInt(KEY_TRANSPARENCY, 100)
    fun isOrderRandom(): Boolean = prefs.getBoolean(KEY_RANDOM, false)
    fun getWidgetGravity(): Int = prefs.getInt(KEY_WIDGET_POSITION, 1)
    fun getBackgroundType(): Int = prefs.getInt(KEY_BACKGROUND_TYPE, 0)
    fun getTypefaceIndex(): Int = prefs.getInt(KEY_TYPEFACE, 0)
    fun getTypeface(): Typeface = Typeface.create(
        when (getTypefaceIndex()) {
            1 -> Typeface.SERIF
            2 -> Typeface.MONOSPACE
            else -> Typeface.SANS_SERIF
        },
        getTypefaceStyle()
    )
    fun getTypefaceStyle(): Int = prefs.getInt(KEY_TYPEFACE_STYLE, 0)
    fun getOutlineSize(): Float = prefs.getFloat(KEY_OUTLINE_SIZE, 8f)

    // Pair<text colour, background colour>
    fun getColours(
        isInverted: Boolean = isInvertedEnabled(),
        transparency: Int = getTransparency(),
        context: Context
    ): Pair<Int, Int> {
        val colours = when (isInverted) {
            true -> Pair(R.color.widget_dark, R.color.widget_light)
            false -> Pair(R.color.widget_light, R.color.widget_dark)
        }

        return Pair(
            context.getColor(colours.first),//Color.parseColor(ColorTransparentUtils.transparentColor(colours.first, 10)),
            Color.parseColor(ColorTransparentUtils.transparentColor(context.getColor(colours.second), transparency * 5))
        )
    }

    fun setSize(size: Pair<Int, Int>) {
        prefs.edit().apply {
            putInt(KEY_AVG_WIDTH, size.first)
            putInt(KEY_AVG_HEIGHT, size.second)
        }.apply()
    }

    fun getSize(): Pair<Int, Int> = Pair(prefs.getInt(KEY_AVG_WIDTH, 1), prefs.getInt(KEY_AVG_HEIGHT, 1))

    fun saveSettings(
        textSize: Int, inverted: Boolean, transparency: Int, random: Boolean,
        widgetPosition: Int, backgroundType: Int, typefaceIndex: Int, typefaceStyleIndex: Int,
        outlineSize: Float
    ) {
        prefs.edit().apply {
            putInt(KEY_TEXT_SIZE, textSize)
            putBoolean(KEY_INVERTED, inverted)
            putInt(KEY_TRANSPARENCY, transparency)
            putBoolean(KEY_RANDOM, random)
            putInt(KEY_WIDGET_POSITION, widgetPosition)
            putInt(KEY_BACKGROUND_TYPE, backgroundType)
            putInt(KEY_TYPEFACE, typefaceIndex)
            putInt(KEY_TYPEFACE_STYLE, typefaceStyleIndex)
            putFloat(KEY_OUTLINE_SIZE, outlineSize)
        }.apply()
    }

    fun saveQuotes(quotes: Array<String>) {
        prefs.edit()
            .putString(KEY_QUOTES, quotes.joinToString(SEPARATOR.toString()))
            .apply()
    }
}