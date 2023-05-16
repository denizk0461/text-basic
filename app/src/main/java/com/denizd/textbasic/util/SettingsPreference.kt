package com.denizd.textbasic.util

import com.denizd.textbasic.db.QuoteStorage

enum class SettingsPreference(val key: String) {
    TEXT_SIZE(QuoteStorage.KEY_TEXT_SIZE),
    TEXT_TRANSPARENCY(QuoteStorage.KEY_TEXT_TRANSPARENCY),
    TEXT_COLOUR("textcolour"),
    HIGHLIGHT_INTENSITY(QuoteStorage.KEY_OUTLINE_SIZE_NEW),
    HIGHLIGHT_TRANSPARENCY(QuoteStorage.KEY_BG_TRANSPARENCY),
    HIGHLIGHT_COLOUR("highlightcolour"),
    ;
}