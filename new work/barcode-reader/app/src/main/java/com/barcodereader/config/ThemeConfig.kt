package com.barcodereader.config

/**
 * Theme Configuration
 *
 * Central place for all theme-related settings.
 */
object ThemeConfig {

    // ==================== THEME MODES ====================

    /** Theme mode options: SYSTEM, LIGHT, DARK, AMOLED */
    const val DEFAULT_THEME = "SYSTEM"

    /** Enable AMOLED theme option */
    const val AMOLED_ENABLED = true

    /** Enable Material You dynamic colors (Android 12+) */
    const val DYNAMIC_COLORS = true

    // ==================== LIGHT THEME COLORS ====================

    const val LIGHT_PRIMARY = 0xFF6750A4L
    const val LIGHT_ON_PRIMARY = 0xFFFFFFFFL
    const val LIGHT_PRIMARY_CONTAINER = 0xFFEADDFFL
    const val LIGHT_ON_PRIMARY_CONTAINER = 0xFF21005DL

    const val LIGHT_SECONDARY = 0xFF625B71L
    const val LIGHT_ON_SECONDARY = 0xFFFFFFFFL
    const val LIGHT_SECONDARY_CONTAINER = 0xFFE8DEF8L
    const val LIGHT_ON_SECONDARY_CONTAINER = 0xFF1D192BL

    const val LIGHT_TERTIARY = 0xFF7D5260L
    const val LIGHT_ON_TERTIARY = 0xFFFFFFFFL
    const val LIGHT_TERTIARY_CONTAINER = 0xFFFFD8E4L
    const val LIGHT_ON_TERTIARY_CONTAINER = 0xFF31111DL

    const val LIGHT_BACKGROUND = 0xFFFFFBFEL
    const val LIGHT_ON_BACKGROUND = 0xFF1C1B1FL
    const val LIGHT_SURFACE = 0xFFFFFBFEL
    const val LIGHT_ON_SURFACE = 0xFF1C1B1FL
    const val LIGHT_SURFACE_VARIANT = 0xFFE7E0ECL
    const val LIGHT_ON_SURFACE_VARIANT = 0xFF49454FL

    const val LIGHT_ERROR = 0xFFB3261EL
    const val LIGHT_ON_ERROR = 0xFFFFFFFFL
    const val LIGHT_ERROR_CONTAINER = 0xFFF9DEDCL
    const val LIGHT_ON_ERROR_CONTAINER = 0xFF410E0BL

    const val LIGHT_OUTLINE = 0xFF79747EL
    const val LIGHT_OUTLINE_VARIANT = 0xFFCAC4D0L

    // ==================== DARK THEME COLORS ====================

    const val DARK_PRIMARY = 0xFFD0BCFFL
    const val DARK_ON_PRIMARY = 0xFF381E72L
    const val DARK_PRIMARY_CONTAINER = 0xFF4F378BL
    const val DARK_ON_PRIMARY_CONTAINER = 0xFFEADDFFL

    const val DARK_SECONDARY = 0xFFCCC2DEL
    const val DARK_ON_SECONDARY = 0xFF332D41L
    const val DARK_SECONDARY_CONTAINER = 0xFF4A4458L
    const val DARK_ON_SECONDARY_CONTAINER = 0xFFE8DEF8L

    const val DARK_TERTIARY = 0xFFEFB8C8L
    const val DARK_ON_TERTIARY = 0xFF492532L
    const val DARK_TERTIARY_CONTAINER = 0xFF633B48L
    const val DARK_ON_TERTIARY_CONTAINER = 0xFFFFD8E4L

    const val DARK_BACKGROUND = 0xFF1C1B1FL
    const val DARK_ON_BACKGROUND = 0xFFE6E1E5L
    const val DARK_SURFACE = 0xFF1C1B1FL
    const val DARK_ON_SURFACE = 0xFFE6E1E5L
    const val DARK_SURFACE_VARIANT = 0xFF49454FL
    const val DARK_ON_SURFACE_VARIANT = 0xFFCAC4D0L

    const val DARK_ERROR = 0xFFF2B8B5L
    const val DARK_ON_ERROR = 0xFF601410L
    const val DARK_ERROR_CONTAINER = 0xFF8C1D18L
    const val DARK_ON_ERROR_CONTAINER = 0xFFF9DEDDL

    const val DARK_OUTLINE = 0xFF938F99L
    const val DARK_OUTLINE_VARIANT = 0xFF49454FL

    // ==================== AMOLED COLORS ====================

    const val AMOLED_BACKGROUND = 0xFF000000L
    const val AMOLED_SURFACE = 0xFF000000L
    const val AMOLED_SURFACE_VARIANT = 0xFF1A1A1AL

    // ==================== TYPOGRAPHY ====================

    /** Font family name (leave empty for system default) */
    const val FONT_FAMILY = ""

    /** Custom font resource (leave 0 for system default) */
    const val CUSTOM_FONT_RES = 0

    // ==================== ANIMATIONS ====================

    /** Page transition duration (ms) */
    const val PAGE_TRANSITION_DURATION = 300

    /** List item animation duration (ms) */
    const val LIST_ANIMATION_DURATION = 200

    /** Scan animation duration (ms) */
    const val SCAN_ANIMATION_DURATION = 2000
}