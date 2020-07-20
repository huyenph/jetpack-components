package com.utildev.workmanager

import android.app.Application
import android.util.Log
import androidx.work.Configuration

class App : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setMinimumLoggingLevel(Log.VERBOSE).build()

}