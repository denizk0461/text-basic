package com.denizd.textbasic.util

import com.denizd.textbasic.db.QuoteStorage

enum class SettingsPreference(val key: String) {
    TEXT_SIZE(QuoteStorage.KEY_TEXT_SIZE),
    TEXT_COLOUR("textcolour"),
    HIGHLIGHT_INTENSITY(QuoteStorage.KEY_OUTLINE_SIZE_NEW),
    HIGHLIGHT_COLOUR("highlightcolour"),

    // Whether a migration from preference storage to Room is needed for the entries
    NEEDS_MIGRATION("needs_migration"),
    ;
}