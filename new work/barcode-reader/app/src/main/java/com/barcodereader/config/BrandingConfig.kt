package com.barcodereader.config

import com.barcodereader.R

/**
 * Branding Configuration
 *
 * Central place for ALL white-label customization.
 * Buyers ONLY need to modify this file to rebrand the entire app.
 *
 * Every value has sensible defaults - app works without changes.
 */
object BrandingConfig {

    // ==================== APP IDENTITY ====================

    /** Display name shown to users */
    const val APP_NAME = "Barcode Scanner"

    /** Package name for the app */
    const val PACKAGE_NAME = "com.barcodereader"

    /** Application ID for Play Store */
    const val APPLICATION_ID = "com.barcodereader"

    // ==================== COMPANY INFO ====================

    /** Company/organization name */
    const val COMPANY_NAME = "Your Company"

    /** Developer name for About screen */
    const val DEVELOPER_NAME = "Your Developer Name"

    /** Company website */
    const val WEBSITE_URL = "https://yourwebsite.com"

    /** Support email */
    const val SUPPORT_EMAIL = "support@example.com"

    // ==================== STORE LINKS ====================

    /** Privacy Policy URL (REQUIRED for Play Store) */
    const val PRIVACY_POLICY_URL = "https://yourwebsite.com/privacy"

    /** Terms of Service URL */
    const val TERMS_URL = "https://yourwebsite.com/terms"

    /** Play Store listing URL */
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=$APPLICATION_ID"

    /** App share URL */
    const val SHARE_URL = "https://play.google.com/store/apps/details?id=$APPLICATION_ID"

    // ==================== CONTACT ====================

    /** Contact email */
    const val CONTACT_EMAIL = "contact@example.com"

    /** Contact phone */
    const val CONTACT_PHONE = "+1 234 567 890"

    /** Contact address */
    const val CONTACT_ADDRESS = "123 Street, City, Country"

    // ==================== BRAND COLORS ====================

    /** Primary brand color (Material You primary) */
    const val PRIMARY_COLOR = 0xFF6750A4L

    /** Secondary brand color */
    const val SECONDARY_COLOR = 0xFF625B71L

    /** Accent color for highlights */
    const val ACCENT_COLOR = 0xFF7D5260L

    // ==================== ICONS ====================

    /** Splash screen logo */
    val SPLASH_LOGO = R.drawable.ic_launcher_foreground

    /** App icon */
    val APP_ICON = R.mipmap.ic_launcher

    /** Round app icon */
    val APP_ICON_ROUND = R.mipmap.ic_launcher_round

    // ==================== VERSION INFO ====================

    /** Display version */
    const val APP_VERSION = "3.0.0"

    /** Build number */
    const val BUILD_NUMBER = 1
}