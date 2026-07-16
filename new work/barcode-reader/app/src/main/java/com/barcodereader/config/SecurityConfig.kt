package com.barcodereader.config

/**
 * Security Configuration
 *
 * All security-related settings in one place.
 */
object SecurityConfig {

    // ==================== LOCK ====================

    /** Enable app lock feature */
    const val APP_LOCK_ENABLED = true

    /** Default lock type: NONE, PIN, PASSWORD, BIOMETRIC */
    const val DEFAULT_LOCK_TYPE = "NONE"

    /** Lock timeout in minutes (0 = immediately) */
    const val LOCK_TIMEOUT_MINUTES = 5

    /** Max failed attempts before lockout */
    const val MAX_FAILED_ATTEMPTS = 5

    /** Lockout duration in minutes */
    const val LOCKOUT_DURATION_MINUTES = 5

    // ==================== PIN ====================

    /** PIN length (4-6) */
    const val PIN_LENGTH = 4

    // ==================== ENCRYPTED STORAGE ====================

    /** Enable encrypted SharedPreferences */
    const val ENCRYPTED_PREFS = true

    // ==================== NETWORK SECURITY ====================

    /** Enable certificate pinning */
    const val CERTIFICATE_PINNING = false

    /** SSL certificate hashes for pinning (SHA-256) */
    val CERTIFICATE_PINS = listOf<String>()

    // ==================== ROOT DETECTION ====================

    /** Enable root detection */
    const val ROOT_DETECTION = false

    /** Show warning on rooted devices */
    const val ROOT_WARNING = true

    /** Block app on rooted devices */
    const val ROOT_BLOCK = false

    // ==================== SCREENSHOT PROTECTION ====================

    /** Enable screenshot prevention */
    const val SCREENSHOT_PROTECTION = false

    // ==================== DATA SECURITY ====================

    /** Clear clipboard on app background */
    const val CLEAR_CLIPBOARD = false

    /** Mask sensitive data in UI */
    const val MASK_SENSITIVE_DATA = true

    // ==================== HELPER ====================

    /**
     * Check if any lock is enabled
     */
    fun isAnyLockEnabled(): Boolean {
        return APP_LOCK_ENABLED && DEFAULT_LOCK_TYPE != "NONE"
    }

    /**
     * Check if biometric is available and enabled
     */
    fun isBiometricEnabled(): Boolean {
        return FeatureFlags.BIOMETRIC_LOCK && DEFAULT_LOCK_TYPE == "BIOMETRIC"
    }
}