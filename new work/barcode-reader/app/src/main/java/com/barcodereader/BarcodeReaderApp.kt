package com.barcodereader

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.barcodereader.config.AppConfig
import javax.inject.Inject
import com.barcodereader.data.local.database.AppDatabase

/**
 * Application class
 *
 * Entry point for Hilt dependency injection.
 * Initializes app-wide configuration.
 */
@HiltAndroidApp
class BarcodeReaderApp : Application() {

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        // Initialize configuration
        AppConfig.init(this)
    }
}