package com.measuremate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.measuremate.core.utils.manualResult
import com.measuremate.data.models.MeasurementEntity
import com.measuremate.data.repository.MeasureMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MeasureMateViewModel(private val repository: MeasureMateRepository) : ViewModel() {
    val projects = repository.projects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val measurements = repository.allMeasurements.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val settings = repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createProject(name: String, folder: String = "General", location: String = "") {
        if (name.isBlank()) return
        viewModelScope.launch { repository.createProject(name.trim(), folder.ifBlank { "General" }, location.trim()) }
    }

    fun archiveProject(id: Long) {
        viewModelScope.launch { repository.archiveProject(id, true) }
    }

    fun duplicateFirstProject(id: Long) {
        viewModelScope.launch {
            repository.project(id)?.let { repository.duplicateProject(it) }
        }
    }

    fun saveManual(projectId: Long, title: String, length: Double, width: Double, height: Double, thickness: Double, unit: String) {
        val result = manualResult(length, width, height)
        viewModelScope.launch {
            repository.saveMeasurement(
                MeasurementEntity(
                    projectId = projectId,
                    title = title.ifBlank { "Manual measurement" },
                    type = "Manual",
                    length = length,
                    width = width,
                    height = height,
                    thickness = thickness,
                    unit = unit,
                    area = result.area,
                    perimeter = result.perimeter,
                    volume = result.volume,
                    surfaceArea = result.surfaceArea
                )
            )
        }
    }

    fun saveArEstimate(projectId: Long, distance: Double, unit: String) {
        viewModelScope.launch {
            repository.saveMeasurement(
                MeasurementEntity(
                    projectId = projectId,
                    title = "AR distance estimate",
                    type = "AR",
                    length = distance,
                    unit = unit
                )
            )
        }
    }

    fun setSetting(key: String, value: String) {
        viewModelScope.launch { repository.setSetting(key, value) }
    }

    companion object {
        fun factory(repository: MeasureMateRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MeasureMateViewModel(repository) as T
        }
    }
}
