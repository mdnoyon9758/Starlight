package com.barcodereader.config

import android.content.Context
import android.content.pm.PackageManager
import com.barcodereader.BuildConfig

/**
 * Application Configuration
 *
 * Central configuration manager that combines all config sources.
 * Provides runtime configuration with fallbacks for missing values.
 */
object AppConfig {

    // ==================== BUILD INFO ====================

    /** App version name */
    val VERSION_NAME: String = BuildConfig.VERSION_NAME

    /** App version code */
    val VERSION_CODE: Int = BuildConfig.VERSION_CODE

    /** Is this a debug build */
    val IS_DEBUG: Boolean = BuildConfig.DEBUG

    // ==================== INITIALIZED STATE ====================

    private var isInitialized = false
    private var appContext: Context? = null

    /**
     * Initialize the configuration
     * Call this once in Application.onCreate()
     */
    fun init(context: Context) {
        if (isInitialized) return
        appContext = context.applicationContext
        isInitialized = true
    }

    // ==================== APP IDENTITY ====================

    /** Display name of the application */
    val appName: String get() = BrandingConfig.APP_NAME

    /** Package name */
    val packageName: String get() = BrandingConfig.PACKAGE_NAME

    /** Application ID */
    val applicationId: String get() = BrandingConfig.APPLICATION_ID

    // ==================== COMPANY INFO ====================

    /** Company name */
    val companyName: String get() = BrandingConfig.COMPANY_NAME

    /** Developer name */
    val developerName: String get() = BrandingConfig.DEVELOPER_NAME

    /** Website URL */
    val websiteUrl: String get() = BrandingConfig.WEBSITE_URL

    /** Support email */
    val supportEmail: String get() = BrandingConfig.SUPPORT_EMAIL

    // ==================== STORE LINKS ====================

    /** Privacy policy URL */
    val privacyPolicyUrl: String get() = BrandingConfig.PRIVACY_POLICY_URL

    /** Terms URL */
    val termsUrl: String get() = BrandingConfig.TERMS_URL

    /** Play Store URL */
    val playStoreUrl: String get() = BrandingConfig.PLAY_STORE_URL

    /** Share URL */
    val shareUrl: String get() = BrandingConfig.SHARE_URL

    // ==================== CONTACT ====================

    /** Contact email */
    val contactEmail: String get() = BrandingConfig.CONTACT_EMAIL

    /** Contact phone */
    val contactPhone: String get() = BrandingConfig.CONTACT_PHONE

    /** Contact address */
    val contactAddress: String get() = BrandingConfig.CONTACT_ADDRESS

    // ==================== FEATURE CHECKS ====================

    /** Is continuous scan enabled */
    val isContinuousScanEnabled: Boolean get() = FeatureFlags.CONTINUOUS_SCAN

    /** Is batch scan enabled */
    val isBatchScanEnabled: Boolean get() = FeatureFlags.BATCH_SCAN

    /** Are QR templates enabled */
    val isQrTemplatesEnabled: Boolean get() = FeatureFlags.QR_TEMPLATES

    /** Is statistics enabled */
    val isStatisticsEnabled: Boolean get() = FeatureFlags.STATISTICS

    /** Is backup enabled */
    val isBackupEnabled: Boolean get() = FeatureFlags.LOCAL_BACKUP

    /** Is security enabled */
    val isSecurityEnabled: Boolean get() = FeatureFlags.PIN_LOCK || FeatureFlags.PASSWORD_LOCK || FeatureFlags.BIOMETRIC_LOCK

    /** Is export enabled */
    val isExportEnabled: Boolean get() = true

    /** Is product lookup enabled */
    val isProductLookupEnabled: Boolean get() = FeatureFlags.PRODUCT_LOOKUP

    // ==================== SERVICE CHECKS ====================

    /** Are ads enabled */
    val isAdsEnabled: Boolean get() = AdConfig.isAdsEnabled()

    /** Is Firebase enabled */
    val isFirebaseEnabled: Boolean get() = FirebaseConfig.isFirebaseAvailable()

    // ==================== THEME ====================

    /** Primary color */
    val primaryColor: Long get() = BrandingConfig.PRIMARY_COLOR

    /** Secondary color */
    val secondaryColor: Long get() = BrandingConfig.SECONDARY_COLOR

    /** Accent color */
    val accentColor: Long get() = BrandingConfig.ACCENT_COLOR

    // ==================== PERMISSIONS ====================

    /**
     * Check if a permission is granted
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if camera permission is available
     */
    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, android.Manifest.permission.CAMERA)
    }

    // ==================== VALIDATION ====================

    /**
     * Validate that all required configurations are set
     * Returns a list of missing configurations (empty if all valid)
     */
    fun validate(): List<String> {
        val missing = mutableListOf<String>()

        if (BrandingConfig.APP_NAME.isBlank()) {
            missing.add("APP_NAME is empty")
        }
        if (BrandingConfig.SUPPORT_EMAIL.isBlank()) {
            missing.add("SUPPORT_EMAIL is empty")
        }
        if (BrandingConfig.PRIVACY_POLICY_URL.isBlank()) {
            missing.add("PRIVACY_POLICY_URL is empty (required for Play Store)")
        }

        return missing
    }

    /**
     * Get configuration summary for debugging
     */
    fun getConfigSummary(): Map<String, Any> {
        return mapOf(
            "appName" to appName,
            "versionName" to VERSION_NAME,
            "versionCode" to VERSION_CODE,
            "isDebug" to IS_DEBUG,
            "isAdsEnabled" to isAdsEnabled,
            "isFirebaseEnabled" to isFirebaseEnabled,
            "isStatisticsEnabled" to isStatisticsEnabled,
            "isBackupEnabled" to isBackupEnabled,
            "isSecurityEnabled" to isSecurityEnabled
        )
    }
}