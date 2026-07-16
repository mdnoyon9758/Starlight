package com.barcodereader.config

/**
 * Feature Flags
 *
 * Toggle features on/off from one place.
 * Disabled features are completely removed at runtime.
 */
object FeatureFlags {

    // ==================== SCANNER ====================

    /** Enable continuous scanning mode */
    const val CONTINUOUS_SCAN = true

    /** Enable batch scanning mode */
    const val BATCH_SCAN = true

    /** Enable flash toggle */
    const val FLASH_TOGGLE = true

    /** Enable zoom control */
    const val ZOOM_CONTROL = true

    /** Enable scan sound */
    const val SCAN_SOUND = true

    /** Enable haptic feedback */
    const val HAPTIC_FEEDBACK = true

    // ==================== GENERATOR ====================

    /** Enable QR templates */
    const val QR_TEMPLATES = true

    /** Enable gradient colors */
    const val GRADIENT_COLORS = true

    /** Enable embedded logo */
    const val EMBEDDED_LOGO = true

    /** Enable custom frames */
    const val CUSTOM_FRAMES = true

    /** Enable custom eyes */
    const val CUSTOM_EYES = true

    /** Enable transparent background */
    const val TRANSPARENT_BACKGROUND = true

    // ==================== HISTORY ====================

    /** Enable folders */
    const val FOLDERS = true

    /** Enable categories */
    const val CATEGORIES = true

    /** Enable tags */
    const val TAGS = true

    /** Enable search */
    const val SEARCH = true

    /** Enable sort */
    const val SORT = true

    /** Enable filter */
    const val FILTER = true

    /** Enable bulk operations */
    const val BULK_OPERATIONS = true

    /** Enable duplicate detection */
    const val DUPLICATE_DETECTION = true

    // ==================== STATISTICS ====================

    /** Enable statistics dashboard */
    const val STATISTICS = true

    // ==================== EXPORT ====================

    /** Enable PNG export */
    const val EXPORT_PNG = true

    /** Enable SVG export */
    const val EXPORT_SVG = true

    /** Enable PDF export */
    const val EXPORT_PDF = true

    /** Enable CSV export */
    const val EXPORT_CSV = true

    /** Enable JSON export */
    const val EXPORT_JSON = true

    /** Enable XLSX export */
    const val EXPORT_XLSX = true

    /** Enable ZIP export */
    const val EXPORT_ZIP = true

    /** Enable ZIP import */
    const val IMPORT_ZIP = true

    // ==================== BACKUP ====================

    /** Enable local backup */
    const val LOCAL_BACKUP = true

    /** Enable Google Drive backup (requires configuration) */
    const val GOOGLE_DRIVE_BACKUP = false

    // ==================== SECURITY ====================

    /** Enable PIN lock */
    const val PIN_LOCK = true

    /** Enable password lock */
    const val PASSWORD_LOCK = true

    /** Enable biometric lock */
    const val BIOMETRIC_LOCK = true

    /** Enable encrypted storage */
    const val ENCRYPTED_STORAGE = true

    /** Enable certificate pinning */
    const val CERTIFICATE_PINNING = false

    /** Enable root detection */
    const val ROOT_DETECTION = false

    /** Enable screenshot protection */
    const val SCREENSHOT_PROTECTION = false

    // ==================== PRODUCT LOOKUP ====================

    /** Enable product lookup via Open Food Facts */
    const val PRODUCT_LOOKUP = true

    // ==================== UI ====================

    /** Enable skeleton loading */
    const val SKELETON_LOADING = true

    /** Enable animations */
    const val ANIMATIONS = true

    /** Enable AMOLED theme option */
    const val AMOLED_THEME = true

    /** Enable Material You dynamic colors */
    const val DYNAMIC_COLORS = true

    // ==================== HELPER ====================

    /**
     * Check if a feature is enabled
     * Returns true if feature exists and is enabled
     */
    fun isEnabled(feature: String): Boolean {
        return when (feature) {
            "continuous_scan" -> CONTINUOUS_SCAN
            "batch_scan" -> BATCH_SCAN
            "flash_toggle" -> FLASH_TOGGLE
            "zoom_control" -> ZOOM_CONTROL
            "qr_templates" -> QR_TEMPLATES
            "statistics" -> STATISTICS
            "local_backup" -> LOCAL_BACKUP
            "pin_lock" -> PIN_LOCK
            "biometric_lock" -> BIOMETRIC_LOCK
            else -> true
        }
    }
}