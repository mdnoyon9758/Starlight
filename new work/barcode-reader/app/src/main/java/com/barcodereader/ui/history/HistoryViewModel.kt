package com.barcodereader.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.barcodereader.domain.usecase.ScanUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * History ViewModel
 *
 * Manages state for the history screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val scanUseCases: ScanUseCases
) : ViewModel() {

    private val _scans = MutableStateFlow<List<ScanHistoryEntity>>(emptyList())
    val scans: StateFlow<List<ScanHistoryEntity>> = _scans.asStateFlow()

    private val _selectedScans = MutableStateFlow<Set<Long>>(emptySet())
    val selectedScans: StateFlow<Set<Long>> = _selectedScans.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow<String?>(null)
    val filterType: StateFlow<String?> = _filterType.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    init {
        loadScans()
    }

    private fun loadScans() {
        viewModelScope.launch {
            scanUseCases.getAllScans().collect { scans ->
                _scans.value = applyFiltersAndSort(scans)
            }
        }
    }

    /**
     * Search scans
     */
    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                scanUseCases.getAllScans().collect { scans ->
                    _scans.value = applyFiltersAndSort(scans)
                }
            } else {
                scanUseCases.searchScans(query).collect { scans ->
                    _scans.value = applyFiltersAndSort(scans)
                }
            }
        }
    }

    /**
     * Set filter type
     */
    fun setFilterType(type: String?) {
        _filterType.value = type
        loadScans()
    }

    /**
     * Set sort order
     */
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        loadScans()
    }

    /**
     * Toggle scan selection
     */
    fun toggleSelection(id: Long) {
        val current = _selectedScans.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedScans.value = current
    }

    /**
     * Select all scans
     */
    fun selectAll() {
        _selectedScans.value = _scans.value.map { it.id }.toSet()
    }

    /**
     * Clear selection
     */
    fun clearSelection() {
        _selectedScans.value = emptySet()
    }

    /**
     * Delete selected scans
     */
    fun deleteSelected() {
        viewModelScope.launch {
            scanUseCases.deleteScans(_selectedScans.value.toList())
            clearSelection()
        }
    }

    /**
     * Toggle favorite for scan
     */
    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            scanUseCases.toggleFavorite(id)
        }
    }

    /**
     * Delete scan
     */
    fun deleteScan(id: Long) {
        viewModelScope.launch {
            scanUseCases.deleteScan(id)
        }
    }

    /**
     * Export selected scans
     */
    fun exportSelected(): List<ScanHistoryEntity> {
        return _scans.value.filter { it.id in _selectedScans.value }
    }

    private fun applyFiltersAndSort(scans: List<ScanHistoryEntity>): List<ScanHistoryEntity> {
        var filtered = scans

        // Apply type filter
        _filterType.value?.let { type ->
            filtered = filtered.filter { it.type == type }
        }

        // Apply sort
        return when (_sortOrder.value) {
            SortOrder.NEWEST -> filtered.sortedByDescending { it.timestamp }
            SortOrder.OLDEST -> filtered.sortedBy { it.timestamp }
            SortOrder.TYPE -> filtered.sortedBy { it.type }
            SortOrder.NAME -> filtered.sortedBy { it.content }
        }
    }
}

/**
 * Sort order options
 */
enum class SortOrder {
    NEWEST,
    OLDEST,
    TYPE,
    NAME
}