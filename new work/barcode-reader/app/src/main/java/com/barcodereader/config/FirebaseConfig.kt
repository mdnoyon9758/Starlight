package com.barcodereader.config

/**
 * Firebase Configuration
 *
 * This file contains all Firebase service settings.
 * Firebase is completely optional - the app works perfectly without it.
 *
 * IMPORTANT: If you don't need Firebase, simply leave FIREBASE_ENABLED as false.
 * The app will not crash or show errors if Firebase is not configured.
 *
 * To enable Firebase:
 * 1. Create a Firebase project at https://console.firebase.google.com
 * 2. Add your Android app to the project
 * 3. Download google-services.json
 * 4. Place it in the app/ directory
 * 5. Set FIREBASE_ENABLED to true
 */
object FirebaseConfig {

    // ==================== MASTER SWITCH ====================

    /**
     * Enable Firebase services
     * Set to false to completely disable Firebase
     */
    const val FIREBASE_ENABLED = false

    // ==================== INDIVIDUAL SERVICES ====================

    /** Enable Firebase Analytics */
    const val ANALYTICS_ENABLED = true

    /** Enable Firebase Crashlytics */
    const val CRASHLYTICS_ENABLED = true

    /** Enable Firebase Remote Config */
    const val REMOTE_CONFIG_ENABLED = true

    /** Enable Firebase Performance Monitoring */
    const val PERFORMANCE_MONITORING_ENABLED = true

    /** Enable Cloud Messaging (Push Notifications) */
    const val PUSH_NOTIFICATIONS_ENABLED = false

    /** Enable Firebase Auth (for Google Drive backup) */
    const val AUTH_ENABLED = false

    /** Enable Cloud Firestore (for cloud sync) */
    const val FIRESTORE_ENABLED = false

    // ==================== ANALYTICS SETTINGS ====================

    /** Track screen views */
    const val TRACK_SCREEN_VIEWS = true

    /** Track user events */
    const val TRACK_EVENTS = true

    /** Track errors */
    const val TRACK_ERRORS = true

    /** Track performance */
    const val TRACK_PERFORMANCE = true

    // ==================== CRASHLYTICS SETTINGS ====================

    /** Enable non-fatal exception logging */
    const val NON_FATAL_EXCEPTIONS = true

    /** Enable custom keys */
    const val CUSTOM_KEYS = true

    /** Enable breadcrumbs */
    const val BREADCRUMBS = true

    // ==================== REMOTE CONFIG SETTINGS ====================

    /**
     * Cache expiration in seconds
     * How long to cache remote config values
     */
    const val CACHE_EXPIRATION_SECONDS = 3600L // 1 hour

    /**
     * Minimum fetch interval in seconds
     * How often to fetch new config from server
     */
    const val MIN_FETCH_INTERVAL_SECONDS = 1800L // 30 minutes

    // ==================== REMOTE CONFIG KEYS ====================

    /** App version override */
    const val KEY_APP_VERSION = "app_version"

    /** Maintenance mode flag */
    const val KEY_MAINTENANCE_MODE = "maintenance_mode"

    /** Feature flags JSON */
    const val KEY_FEATURE_FLAGS = "feature_flags"

    /** Force update version */
    const val KEY_FORCE_UPDATE_VERSION = "force_update_version"

    /** Minimum supported version */
    const val KEY_MIN_SUPPORTED_VERSION = "min_supported_version"

    /** Welcome message */
    const val KEY_WELCOME_MESSAGE = "welcome_message"

    /** Promotional message */
    const val KEY_PROMO_MESSAGE = "promo_message"

    // ==================== PERFORMANCE SETTINGS ====================

    /** Monitor network requests */
    const val MONITOR_NETWORK = true

    /** Monitor screen rendering */
    const val MONITOR_SCREEN_RENDERING = true

    /** Monitor startup time */
    const val MONITOR_STARTUP = true

    // ==================== PUSH NOTIFICATION SETTINGS ====================

    /** Notification channel ID */
    const val NOTIFICATION_CHANNEL_ID = "barcode_reader_channel"

    /** Notification channel name */
    const val NOTIFICATION_CHANNEL_NAME = "Barcode Reader Notifications"

    /** Notification channel description */
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications from Barcode Reader"

    /** Default notification icon */
    val NOTIFICATION_ICON = com.barcodereader.R.drawable.ic_launcher_foreground

    // ==================== HELPER FUNCTIONS ====================

    /**
     * Check if Firebase is available and enabled
     */
    fun isFirebaseAvailable(): Boolean {
        if (!FIREBASE_ENABLED) return false
        return try {
            Class.forName("com.google.firebase.FirebaseApp")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    /**
     * Check if Analytics is available
     */
    fun isAnalyticsAvailable(): Boolean {
        return isFirebaseAvailable() && ANALYTICS_ENABLED
    }

    /**
     * Check if Crashlytics is available
     */
    fun isCrashlyticsAvailable(): Boolean {
        return isFirebaseAvailable() && CRASHLYTICS_ENABLED
    }

    /**
     * Check if Remote Config is available
     */
    fun isRemoteConfigAvailable(): Boolean {
        return isFirebaseAvailable() && REMOTE_CONFIG_ENABLED
    }

    /**
     * Check if Performance Monitoring is available
     */
    fun isPerformanceAvailable(): Boolean {
        return isFirebaseAvailable() && PERFORMANCE_MONITORING_ENABLED
    }

    /**
     * Check if Push Notifications are available
     */
    fun isPushNotificationsAvailable(): Boolean {
        return isFirebaseAvailable() && PUSH_NOTIFICATIONS_ENABLED
    }

    /**
     * Check if Auth is available
     */
    fun isAuthAvailable(): Boolean {
        return isFirebaseAvailable() && AUTH_ENABLED
    }

    /**
     * Check if Firestore is available
     */
    fun isFirestoreAvailable(): Boolean {
        return isFirebaseAvailable() && FIRESTORE_ENABLED
    }
}