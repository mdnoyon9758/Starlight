package com.barcodereader.config

import com.barcodereader.R

/**
 * White Label Configuration
 *
 * This file contains all configurable settings for white-label customization.
 * Buyers can modify these values to rebrand the application.
 *
 * IMPORTANT: All values have sensible defaults. The app will work without any changes.
 */
object WhiteLabelConfig {

    // ==================== APP IDENTITY ====================

    /** Display name of the application */
    const val APP_NAME = "Barcode Scanner"

    /** Package name for the application */
    const val PACKAGE_NAME = "com.barcodereader"

    /** Application ID for Play Store */
    const val APPLICATION_ID = "com.barcodereader"

    // ==================== COMPANY INFORMATION ====================

    /** Company or organization name */
    const val COMPANY_NAME = "Your Company"

    /** Developer name shown in About screen */
    const val DEVELOPER_NAME = "Your Developer Name"

    /** Company website URL */
    const val WEBSITE_URL = "https://yourwebsite.com"

    /** Support email address */
    const val SUPPORT_EMAIL = "support@example.com"

    // ==================== STORE LINKS ====================

    /** Privacy Policy URL (required for Play Store) */
    const val PRIVACY_POLICY_URL = "https://yourwebsite.com/privacy"

    /** Terms of Service URL */
    const val TERMS_URL = "https://yourwebsite.com/terms"

    /** Play Store listing URL */
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=$APPLICATION_ID"

    /** Share URL for the app */
    const val SHARE_URL = "https://play.google.com/store/apps/details?id=$APPLICATION_ID"

    // ==================== BRANDING COLORS ====================

    /** Primary brand color (Material You primary) */
    const val PRIMARY_COLOR = 0xFF6750A4L

    /** Secondary brand color */
    const val SECONDARY_COLOR = 0xFF625B71L

    /** Accent color for highlights */
    const val ACCENT_COLOR = 0xFF7D5260L

    /** Background color for light theme */
    const val LIGHT_BACKGROUND = 0xFFFFFBFEL

    /** Surface color for light theme */
    const val LIGHT_SURFACE = 0xFFFFFBFEL

    /** Background color for dark theme */
    const val DARK_BACKGROUND = 0xFF1C1B1FL

    /** Surface color for dark theme */
    const val DARK_SURFACE = 0xFF1C1B1FL

    // ==================== ICONS ====================

    /** Splash screen logo resource */
    val SPLASH_LOGO = R.drawable.ic_launcher_foreground

    /** App icon resource */
    val APP_ICON = R.mipmap.ic_launcher

    /** Round app icon resource */
    val APP_ICON_ROUND = R.mipmap.ic_launcher_round

    // ==================== CONTACT INFORMATION ====================

    /** Contact email for user inquiries */
    const val CONTACT_EMAIL = "contact@example.com"

    /** Contact phone number */
    const val CONTACT_PHONE = "+1 234 567 890"

    /** Contact address */
    const val CONTACT_ADDRESS = "123 Street, City, Country"

    // ==================== APP BEHAVIOR ====================

    /** Maximum number of history items to keep */
    const val MAX_HISTORY_ITEMS = 500

    /** Enable duplicate detection in history */
    const val DUPLICATE_DETECTION_ENABLED = true

    /** Enable haptic feedback by default */
    const val HAPTIC_FEEDBACK_DEFAULT = true

    /** Enable scan sound by default */
    const val SCAN_SOUND_DEFAULT = true

    /** Enable auto-focus by default */
    const val AUTO_FOCUS_DEFAULT = true

    // ==================== SCANNER SETTINGS ====================

    /** Default scan region size (percentage of screen) */
    const val SCAN_REGION_SIZE = 0.7f

    /** Scan animation duration in milliseconds */
    const val SCAN_ANIMATION_DURATION = 2000L

    /** Continuous scan timeout in milliseconds */
    const val CONTINUOUS_SCAN_TIMEOUT = 30000L

    // ==================== GENERATOR SETTINGS ====================

    /** Default QR code size in pixels */
    const val DEFAULT_QR_SIZE = 800

    /** Default barcode width in pixels */
    const val DEFAULT_BARCODE_WIDTH = 1024

    /** Default barcode height in pixels */
    const val DEFAULT_BARCODE_HEIGHT = 256

    /** Default error correction level for QR codes */
    const val DEFAULT_ERROR_CORRECTION = "M"

    /** Default margin for generated codes */
    const val DEFAULT_MARGIN = 2

    // ==================== EXPORT SETTINGS ====================

    /** Default export format */
    const val DEFAULT_EXPORT_FORMAT = "PNG"

    /** High resolution export multiplier */
    const val HIGH_RES_MULTIPLIER = 2

    /** Enable print support */
    const val PRINT_SUPPORT_ENABLED = true

    // ==================== BACKUP SETTINGS ====================

    /** Enable automatic backup */
    const val AUTO_BACKUP_ENABLED = false

    /** Automatic backup interval in hours */
    const val AUTO_BACKUP_INTERVAL = 24

    /** Maximum number of local backups */
    const val MAX_LOCAL_BACKUPS = 10

    // ==================== SECURITY SETTINGS ====================

    /** Enable security features */
    const val SECURITY_ENABLED = true

    /** Default lock timeout in minutes (0 = immediately) */
    const val DEFAULT_LOCK_TIMEOUT = 5

    /** Maximum failed attempts before lockout */
    const val MAX_FAILED_ATTEMPTS = 5

    /** Lockout duration in minutes */
    const val LOCKOUT_DURATION = 5

    // ==================== STATISTICS ====================

    /** Enable statistics tracking */
    const val STATISTICS_ENABLED = true

    /** Statistics retention period in days */
    const val STATISTICS_RETENTION_DAYS = 365

    // ==================== AD MOB ====================

    /** Enable ads by default (can be overridden by AdConfig) */
    const val ADS_DEFAULT_ENABLED = false

    // ==================== FIREBASE ====================

    /** Enable Firebase by default (can be overridden by FirebaseConfig) */
    const val FIREBASE_DEFAULT_ENABLED = false

    // ==================== UI SETTINGS ====================

    /** Enable Material You dynamic colors (Android 12+) */
    const val DYNAMIC_COLORS_ENABLED = true

    /** Enable AMOLED dark theme option */
    const val AMOLED_THEME_ENABLED = true

    /** Enable skeleton loading */
    const val SKELETON_LOADING_ENABLED = true

    /** Enable animations */
    const val ANIMATIONS_ENABLED = true

    /** Default theme mode (SYSTEM, LIGHT, DARK, AMOLED) */
    const val DEFAULT_THEME_MODE = "SYSTEM"

    // ==================== LOCALIZATION ====================

    /** Default language code */
    const val DEFAULT_LANGUAGE = "en"

    /** Supported language codes */
    val SUPPORTED_LANGUAGES = listOf("en", "es", "de", "fr", "ar", "hi")

    // ==================== LEGAL ====================

    /** Open source licenses URL */
    const val LICENSES_URL = "https://yourwebsite.com/licenses"

    /** App version for display */
    const val APP_VERSION = "3.0.0"

    /** Build number */
    const val BUILD_NUMBER = 1

    // ==================== FEATURE FLAGS ====================

    /** Enable continuous scanning feature */
    const val FEATURE_CONTINUOUS_SCAN = true

    /** Enable batch scanning feature */
    const val FEATURE_BATCH_SCAN = true

    /** Enable QR templates feature */
    const val FEATURE_QR_TEMPLATES = true

    /** Enable statistics feature */
    const val FEATURE_STATISTICS = true

    /** Enable backup feature */
    const val FEATURE_BACKUP = true

    /** Enable security feature */
    const val FEATURE_SECURITY = true

    /** Enable export feature */
    const val FEATURE_EXPORT = true

    /** Enable product lookup feature */
    const val FEATURE_PRODUCT_LOOKUP = true
}