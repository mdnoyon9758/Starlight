package com.barcodereader.config

/**
 * API Configuration
 *
 * Network and API settings in one place.
 */
object ApiConfig {

    // ==================== OPEN FOOD FACTS ====================

    /** Enable product lookup */
    const val PRODUCT_LOOKUP_ENABLED = true

    /** Open Food Facts API base URL */
    const val OFF_API_BASE_URL = "https://world.openfoodfacts.org/api/v2/product/"

    /** Open Food Facts image base URL */
    const val OFF_IMAGE_BASE_URL = "https://images.openfoodfacts.org/images/products/"

    /** API timeout in milliseconds */
    const val API_TIMEOUT_MS = 5000L

    /** User-Agent string */
    const val USER_AGENT = "BarcodeScanner/3.0"

    // ==================== CACHE ====================

    /** Product info cache size */
    const val PRODUCT_CACHE_SIZE = 100

    /** Image cache size */
    const val IMAGE_CACHE_SIZE = 50

    /** Cache expiration in milliseconds (1 hour) */
    const val CACHE_EXPIRATION_MS = 3600000L

    // ==================== RATE LIMITING ====================

    /** Enable rate limiting */
    const val RATE_LIMITING = true

    /** Max requests per minute */
    const val MAX_REQUESTS_PER_MINUTE = 60

    // ==================== RETRY ====================

    /** Enable automatic retry */
    const val AUTO_RETRY = true

    /** Max retry attempts */
    const val MAX_RETRY_ATTEMPTS = 3

    /** Retry delay in milliseconds */
    const val RETRY_DELAY_MS = 1000L

    // ==================== PROXY ====================

    /** Use proxy (for testing) */
    const val USE_PROXY = false

    /** Proxy host */
    const val PROXY_HOST = ""

    /** Proxy port */
    const val PROXY_PORT = 0

    // ==================== LOGGING ====================

    /** Enable API logging (debug only) */
    const val API_LOGGING = false

    /** Log request/response bodies */
    const val LOG_BODIES = false
}