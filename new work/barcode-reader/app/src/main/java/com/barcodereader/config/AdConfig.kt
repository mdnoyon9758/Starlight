package com.barcodereader.config

/**
 * AdMob Configuration
 *
 * This file contains all AdMob settings for monetization.
 * All values are placeholders - buyers should replace with their own AdMob IDs.
 *
 * IMPORTANT: If ADMOB_APP_ID is empty, all ads are automatically disabled.
 * The app will work perfectly without any ad configuration.
 */
object AdConfig {

    // ==================== ADMOB IDs ====================

    /**
     * AdMob App ID
     * Get this from: https://apps.admob.com
     * Format: ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX
     * Leave empty to disable all ads.
     */
    const val ADMOB_APP_ID = ""

    /**
     * Banner Ad Unit ID
     * For displaying banner ads at bottom of screens
     */
    const val BANNER_AD_UNIT_ID = ""

    /**
     * Interstitial Ad Unit ID
     * For full-screen ads shown at natural breaks
     */
    const val INTERSTITIAL_AD_UNIT_ID = ""

    /**
     * Rewarded Ad Unit ID
     * For opt-in ads that reward users
     */
    const val REWARDED_AD_UNIT_ID = ""

    /**
     * App Open Ad Unit ID
     * For ads shown when app is opened
     */
    const val APP_OPEN_AD_UNIT_ID = ""

    /**
     * Native Ad Unit ID
     * For ads that match app's UI
     */
    const val NATIVE_AD_UNIT_ID = ""

    // ==================== AD BEHAVIOR ====================

    /** Master switch for ads (must be true AND ADMOB_APP_ID must be non-empty) */
    const val ADS_ENABLED = false

    /** Show banner ads on screens */
    const val SHOW_BANNER_ADS = true

    /** Show interstitial ads */
    const val SHOW_INTERSTITIAL_ADS = true

    /** Show rewarded ads */
    const val SHOW_REWARDED_ADS = true

    /** Show app open ads */
    const val SHOW_APP_OPEN_ADS = true

    /** Show native ads in lists */
    const val SHOW_NATIVE_ADS = true

    // ==================== FREQUENCY & TIMING ====================

    /**
     * Show interstitial ad every N scans
     * Set to 0 to disable frequency-based interstitials
     */
    const val INTERSTITIAL_FREQUENCY = 5

    /**
     * Minimum seconds between interstitial ads
     */
    const val INTERSTITIAL_MIN_INTERVAL_SECONDS = 30

    /**
     * Show app open ad on cold start only
     */
    const val APP_OPEN_COLD_START_ONLY = true

    /**
     * Minimum seconds between app open ads
     */
    const val APP_OPEN_MIN_INTERVAL_SECONDS = 120

    // ==================== BANNER SETTINGS ====================

    /**
     * Banner refresh rate in seconds (minimum 30)
     */
    const val BANNER_REFRESH_RATE = 60

    /**
     * Banner ad position
     * "BOTTOM" or "TOP"
     */
    const val BANNER_POSITION = "BOTTOM"

    // ==================== REWARDED SETTINGS ====================

    /**
     * Reward type for rewarded ads
     */
    const val REWARD_TYPE = "bonus_scan"

    /**
     * Reward amount
     */
    const val REWARD_AMOUNT = 1

    // ==================== AD PLACEMENT ====================

    /** Show ads on Scan screen */
    const val ADS_ON_SCAN = true

    /** Show ads on History screen */
    const val ADS_ON_HISTORY = true

    /** Show ads on Generate screen */
    const val ADS_ON_GENERATE = true

    /** Show ads on Statistics screen */
    const val ADS_ON_STATISTICS = true

    /** Show ads on Settings screen */
    const val ADS_ON_SETTINGS = false

    /** Show ads on result sheet */
    const val ADS_ON_RESULT = true

    // ==================== TEST MODE ====================

    /** Enable test ads (use for development only) */
    const val TEST_ADS_ENABLED = false

    /**
     * Test device IDs
     * Add your device ID here for test ads during development
     * Get your device ID from logcat when running a test ad
     */
    val TEST_DEVICE_IDS = listOf(
        "YOUR_TEST_DEVICE_ID"
    )

    // ==================== HELPER FUNCTIONS ====================

    /**
     * Check if ads are enabled
     * Ads are enabled only if master switch is on AND App ID is configured
     */
    fun isAdsEnabled(): Boolean {
        return ADS_ENABLED && ADMOB_APP_ID.isNotBlank()
    }

    /**
     * Check if banner ads are available
     */
    fun isBannerAdAvailable(): Boolean {
        return isAdsEnabled() && SHOW_BANNER_ADS && BANNER_AD_UNIT_ID.isNotBlank()
    }

    /**
     * Check if interstitial ads are available
     */
    fun isInterstitialAdAvailable(): Boolean {
        return isAdsEnabled() && SHOW_INTERSTITIAL_ADS && INTERSTITIAL_AD_UNIT_ID.isNotBlank()
    }

    /**
     * Check if rewarded ads are available
     */
    fun isRewardedAdAvailable(): Boolean {
        return isAdsEnabled() && SHOW_REWARDED_ADS && REWARDED_AD_UNIT_ID.isNotBlank()
    }

    /**
     * Check if app open ads are available
     */
    fun isAppOpenAdAvailable(): Boolean {
        return isAdsEnabled() && SHOW_APP_OPEN_ADS && APP_OPEN_AD_UNIT_ID.isNotBlank()
    }

    /**
     * Check if native ads are available
     */
    fun isNativeAdAvailable(): Boolean {
        return isAdsEnabled() && SHOW_NATIVE_ADS && NATIVE_AD_UNIT_ID.isNotBlank()
    }

    /**
     * Should show interstitial based on scan count
     */
    fun shouldShowInterstitial(scanCount: Int): Boolean {
        if (!isInterstitialAdAvailable()) return false
        if (INTERSTITIAL_FREQUENCY <= 0) return false
        return scanCount % INTERSTITIAL_FREQUENCY == 0
    }
}