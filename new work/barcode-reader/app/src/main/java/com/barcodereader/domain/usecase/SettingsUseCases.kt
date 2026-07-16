package com.barcodereader.domain.usecase

import com.barcodereader.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Settings Use Cases
 *
 * Grouped use cases for settings operations.
 */
class SettingsUseCases @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Get theme mode
     */
    fun getThemeMode(): Flow<String> = settingsRepository.getThemeMode()

    /**
     * Set theme mode
     */
    suspend fun setThemeMode(mode: String) = settingsRepository.setThemeMode(mode)

    /**
     * Get dynamic colors
     */
    fun getDynamicColors(): Flow<Boolean> = settingsRepository.getDynamicColors()

    /**
     * Set dynamic colors
     */
    suspend fun setDynamicColors(enabled: Boolean) = settingsRepository.setDynamicColors(enabled)

    /**
     * Get scan sound
     */
    fun getScanSound(): Flow<Boolean> = settingsRepository.getScanSound()

    /**
     * Set scan sound
     */
    suspend fun setScanSound(enabled: Boolean) = settingsRepository.setScanSound(enabled)

    /**
     * Get haptic feedback
     */
    fun getHapticFeedback(): Flow<Boolean> = settingsRepository.getHapticFeedback()

    /**
     * Set haptic feedback
     */
    suspend fun setHapticFeedback(enabled: Boolean) = settingsRepository.setHapticFeedback(enabled)

    /**
     * Get auto focus
     */
    fun getAutoFocus(): Flow<Boolean> = settingsRepository.getAutoFocus()

    /**
     * Set auto focus
     */
    suspend fun setAutoFocus(enabled: Boolean) = settingsRepository.setAutoFocus(enabled)

    /**
     * Get lock type
     */
    fun getLockType(): Flow<String> = settingsRepository.getLockType()

    /**
     * Set lock type
     */
    suspend fun setLockType(type: String) = settingsRepository.setLockType(type)

    /**
     * Get lock enabled
     */
    fun getLockEnabled(): Flow<Boolean> = settingsRepository.getLockEnabled()

    /**
     * Set lock enabled
     */
    suspend fun setLockEnabled(enabled: Boolean) = settingsRepository.setLockEnabled(enabled)

    /**
     * Get language
     */
    fun getLanguage(): Flow<String> = settingsRepository.getLanguage()

    /**
     * Set language
     */
    suspend fun setLanguage(language: String) = settingsRepository.setLanguage(language)
}