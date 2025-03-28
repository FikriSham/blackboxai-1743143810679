package com.meerkat.converter

import android.app.Application
import com.meerkat.converter.database.HistoryDatabase
import timber.log.Timber

class MeerkatApplication : Application() {
    val database: HistoryDatabase by lazy {
        HistoryDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
