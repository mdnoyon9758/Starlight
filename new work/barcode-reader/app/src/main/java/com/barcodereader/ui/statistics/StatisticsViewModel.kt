package com.barcodereader.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcodereader.domain.usecase.ScanUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Statistics ViewModel
 *
 * Manages state for the statistics screen.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val scanUseCases: ScanUseCases
) : ViewModel() {

    private val _scanCount = MutableStateFlow(0)
    val scanCount: StateFlow<Int> = _scanCount.asStateFlow()

    private val _favoriteCount = MutableStateFlow(0)
    val favoriteCount: StateFlow<Int> = _favoriteCount.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            scanUseCases.getScanCount().collect { count ->
                _scanCount.value = count
            }
        }
        viewModelScope.launch {
            scanUseCases.getFavoriteCount().collect { count ->
                _favoriteCount.value = count
            }
        }
    }
}