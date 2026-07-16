package com.barcodereader.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.barcodereader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Settings Repository
 *
 * Handles app settings and preferences.
 * Uses DataStore for persistent storage.
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    companion object {
        // Theme keys
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")

        // Scanner keys
        val SCAN_SOUND = booleanPreferencesKey("scan_sound")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val AUTO_FOCUS = booleanPreferencesKey("auto_focus")

        // Security keys
        val LOCK_TYPE = stringPreferencesKey("lock_type")
        val LOCK_ENABLED = booleanPreferencesKey("lock_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")

        // Language keys
        val LANGUAGE = stringPreferencesKey("language")
    }

    /**
     * Get theme mode
     */
    fun getThemeMode(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[THEME_MODE] ?: "SYSTEM"
        }
    }

    /**
     * Set theme mode
     */
    suspend fun setThemeMode(mode: String) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[THEME_MODE] = mode
            }
        }
    }

    /**
     * Get dynamic colors setting
     */
    fun getDynamicColors(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DYNAMIC_COLORS] ?: true
        }
    }

    /**
     * Set dynamic colors
     */
    suspend fun setDynamicColors(enabled: Boolean) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[DYNAMIC_COLORS] = enabled
            }
        }
    }

    /**
     * Get scan sound setting
     */
    fun getScanSound(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[SCAN_SOUND] ?: true
        }
    }

    /**
     * Set scan sound
     */
    suspend fun setScanSound(enabled: Boolean) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[SCAN_SOUND] = enabled
            }
        }
    }

    /**
     * Get haptic feedback setting
     */
    fun getHapticFeedback(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[HAPTIC_FEEDBACK] ?: true
        }
    }

    /**
     * Set haptic feedback
     */
    suspend fun setHapticFeedback(enabled: Boolean) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[HAPTIC_FEEDBACK] = enabled
            }
        }
    }

    /**
     * Get auto focus setting
     */
    fun getAutoFocus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[AUTO_FOCUS] ?: true
        }
    }

    /**
     * Set auto focus
     */
    suspend fun setAutoFocus(enabled: Boolean) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[AUTO_FOCUS] = enabled
            }
        }
    }

    /**
     * Get lock type
     */
    fun getLockType(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[LOCK_TYPE] ?: "NONE"
        }
    }

    /**
     * Set lock type
     */
    suspend fun setLockType(type: String) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[LOCK_TYPE] = type
            }
        }
    }

    /**
     * Get lock enabled setting
     */
    fun getLockEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOCK_ENABLED] ?: false
        }
    }

    /**
     * Set lock enabled
     */
    suspend fun setLockEnabled(enabled: Boolean) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[LOCK_ENABLED] = enabled
            }
        }
    }

    /**
     * Get language
     */
    fun getLanguage(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[LANGUAGE] ?: "en"
        }
    }

    /**
     * Set language
     */
    suspend fun setLanguage(language: String) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[LANGUAGE] = language
            }
        }
    }
}