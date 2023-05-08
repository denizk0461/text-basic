package com.denizd.textbasic

import android.app.backup.*

class DriveAgent : BackupAgentHelper() {

    override fun onCreate() {
        super.onCreate()

        SharedPreferencesBackupHelper(this, QuoteStorage.PREF_NAME).also {
            addHelper(QuoteStorage.PREF_BACKUP_KEY, it)
        }
    }
}