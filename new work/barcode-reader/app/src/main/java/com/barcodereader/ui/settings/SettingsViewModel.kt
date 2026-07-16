package com.barcodereader.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcodereader.domain.usecase.SettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings ViewModel
 *
 * Manages state for the settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _themeMode = MutableStateFlow("SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColors = MutableStateFlow(true)
    val dynamicColors: StateFlow<Boolean> = _dynamicColors.asStateFlow()

    private val _scanSound = MutableStateFlow(true)
    val scanSound: StateFlow<Boolean> = _scanSound.asStateFlow()

    private val _hapticFeedback = MutableStateFlow(true)
    val hapticFeedback: StateFlow<Boolean> = _hapticFeedback.asStateFlow()

    private val _autoFocus = MutableStateFlow(true)
    val autoFocus: StateFlow<Boolean> = _autoFocus.asStateFlow()

    private val _lockType = MutableStateFlow("NONE")
    val lockType: StateFlow<String> = _lockType.asStateFlow()

    private val _lockEnabled = MutableStateFlow(false)
    val lockEnabled: StateFlow<Boolean> = _lockEnabled.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsUseCases.getThemeMode().collect { _themeMode.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getDynamicColors().collect { _dynamicColors.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getScanSound().collect { _scanSound.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getHapticFeedback().collect { _hapticFeedback.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getAutoFocus().collect { _autoFocus.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getLockType().collect { _lockType.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getLockEnabled().collect { _lockEnabled.value = it }
        }
        viewModelScope.launch {
            settingsUseCases.getLanguage().collect { _language.value = it }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsUseCases.setThemeMode(mode)
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsUseCases.setDynamicColors(enabled)
        }
    }

    fun setScanSound(enabled: Boolean) {
        viewModelScope.launch {
            settingsUseCases.setScanSound(enabled)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            settingsUseCases.setHapticFeedback(enabled)
        }
    }

    fun setAutoFocus(enabled: Boolean) {
        viewModelScope.launch {
            settingsUseCases.setAutoFocus(enabled)
        }
    }

    fun setLockType(type: String) {
        viewModelScope.launch {
            settingsUseCases.setLockType(type)
        }
    }

    fun setLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsUseCases.setLockEnabled(enabled)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsUseCases.setLanguage(language)
        }
    }
}