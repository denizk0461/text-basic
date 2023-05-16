package com.denizd.textbasic.db

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.denizd.textbasic.util.ColorTransparentUtils
import com.denizd.textbasic.R
import com.denizd.textbasic.util.SettingsPreference

class QuoteStorage private constructor(context: Context) {

    companion object {

        private lateinit var storage: QuoteStorage

        fun getInstance(context: Context): QuoteStorage {
            if (!::storage.isInitialized) {
                synchronized(Object::class.java) {
                    storage = QuoteStorage(context)
                }
            }
            return storage
        }

        const val PREF_NAME = "textbasicprefs"
        const val PREF_BACKUP_KEY = "textbasicprefskey"
        const val SEPARATOR = '\uFFFF'

        private const val KEY_QUOTES = "quotes"
        private const val KEY_QUOTE_COUNTER = "quotecounter"
        const val KEY_TEXT_SIZE = "textsize"
        private const val KEY_INVERTED = "inverted"
        private const val KEY_HIGH_CONTRAST = "hicontrast"
        const val KEY_TEXT_TRANSPARENCY = "transparency_text"
        const val KEY_BG_TRANSPARENCY = "transparency"
        private const val KEY_RANDOM = "random"
        private const val KEY_WIDGET_POSITION = "widgetposition"
        private const val KEY_BACKGROUND_TYPE = "backgroundtype"
        private const val KEY_TYPEFACE = "typeface"
        private const val KEY_TYPEFACE_STYLE = "typefacestyle"
        private const val KEY_AVG_WIDTH = "avgwidth"
        private const val KEY_AVG_HEIGHT = "avgheight"
        private const val KEY_OUTLINE_SIZE = "outlinesize"
        const val KEY_OUTLINE_SIZE_NEW = "outlinesizeint"
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
    fun setTextSize(newValue: Int) {
        prefs.edit().putInt(KEY_TEXT_SIZE, newValue).apply()
    }

    fun getInt(
        pref: SettingsPreference,
        defaultValue: Int = 0,
    ): Int = prefs.getInt(pref.key, defaultValue)
    fun setInt(
        pref: SettingsPreference,
        newValue: Int,
    ) {
        prefs.edit().putInt(pref.key, newValue).apply()
    }

    fun isInvertedEnabled(): Boolean = prefs.getBoolean(KEY_INVERTED, false)
    fun getTextTransparency(): Int = prefs.getInt(KEY_TEXT_TRANSPARENCY, 100)
    fun setTextTransparency(newValue: Int) {
        prefs.edit().putInt(KEY_TEXT_TRANSPARENCY, newValue).apply()
    }
    fun getBackgroundTransparency(): Int = prefs.getInt(KEY_BG_TRANSPARENCY, 100)
    fun setBackgroundTransparency(newValue: Int) {
        prefs.edit().putInt(KEY_BG_TRANSPARENCY, newValue).apply()
    }
    fun isOrderRandom(): Boolean = prefs.getBoolean(KEY_RANDOM, false)
    fun setIsOrderRandom(newValue: Boolean) {
        prefs.edit().putBoolean(KEY_RANDOM, newValue).apply()
    }

    fun getWidgetGravity(): Int = prefs.getInt(KEY_WIDGET_POSITION, 1)
    fun setWidgetGravity(newValue: Int) {
        prefs.edit().putInt(KEY_WIDGET_POSITION, newValue).apply()
    }
    fun getBackgroundType(): Int = prefs.getInt(KEY_BACKGROUND_TYPE, 3) // default none
    fun setBackgroundType(newValue: Int) {
        prefs.edit().putInt(KEY_BACKGROUND_TYPE, newValue).apply()
    }
    fun getTypefaceIndex(): Int = prefs.getInt(KEY_TYPEFACE, 0)
    fun setTypefaceIndex(newValue: Int) {
        prefs.edit().putInt(KEY_TYPEFACE, newValue).apply()
    }
    fun getTypeface(): Typeface = Typeface.create(
        when (getTypefaceIndex()) {
            1 -> Typeface.SERIF
            2 -> Typeface.MONOSPACE
            else -> Typeface.SANS_SERIF
        },
        when (getTypefaceStyle()) {
            1 -> Typeface.BOLD
            2 -> Typeface.ITALIC
            3 -> Typeface.BOLD_ITALIC
            else -> Typeface.NORMAL
        }
    )
    fun getTypefaceStyle(): Int = prefs.getInt(KEY_TYPEFACE_STYLE, 0)
    fun setTypefaceStyle(newValue: Int) {
        prefs.edit().putInt(KEY_TYPEFACE_STYLE, newValue).apply()
    }
    fun getOutlineSize(): Float = prefs.getFloat(KEY_OUTLINE_SIZE, 8f)
    fun getOutlineSizeNew(): Int = prefs.getInt(KEY_OUTLINE_SIZE_NEW, 8)

    fun getTextColour(): String = prefs.getString(SettingsPreference.TEXT_COLOUR.key, "ffffff") ?: "ffffff"
    fun setTextColour(newValue: String) {
        prefs.edit().putString(SettingsPreference.TEXT_COLOUR.key, newValue).apply()
    }

    fun getHighlightColour(): String = prefs.getString(SettingsPreference.HIGHLIGHT_COLOUR.key, "000000") ?: "000000"
    fun setHighlightColour(newValue: String) {
        prefs.edit().putString(SettingsPreference.HIGHLIGHT_COLOUR.key, newValue).apply()
    }

    // Pair<text colour, background colour>
    fun getColours(
        isInverted: Boolean = isInvertedEnabled(),
        textTransparency: Int = getTextTransparency(),
        bgTransparency: Int = getBackgroundTransparency(),
        context: Context
    ): Pair<Int, Int> {
        val colours = when (isInverted) {
            true -> Pair(R.color.widget_dark, R.color.widget_light)
            false -> Pair(R.color.widget_light, R.color.widget_dark)
        }

        return Pair(
            Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    context.getColor(colours.first),
                    textTransparency,
                )
            ),//Color.parseColor(ColorTransparentUtils.transparentColor(colours.first, 10)),
            Color.parseColor(
                ColorTransparentUtils.transparentColor(
                    context.getColor(colours.second),
                    bgTransparency,
                )
            )
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
        textSize: Int,
        inverted: Boolean,
        textTransparency: Int,
        bgTransparency: Int,
        random: Boolean,
        widgetPosition: Int,
        backgroundType: Int,
        typefaceIndex: Int,
        typefaceStyleIndex: Int,
        outlineSize: Float,
    ) {
        prefs.edit().apply {
            putInt(KEY_TEXT_SIZE, textSize)
            putBoolean(KEY_INVERTED, inverted)
            putInt(KEY_TEXT_TRANSPARENCY, textTransparency)
            putInt(KEY_BG_TRANSPARENCY, bgTransparency)
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