package com.measuremate.data.repository

import com.measuremate.data.database.MeasureMateDao
import com.measuremate.data.models.MeasurementEntity
import com.measuremate.data.models.NoteEntity
import com.measuremate.data.models.ProjectEntity
import com.measuremate.data.models.SettingEntity

class MeasureMateRepository(private val dao: MeasureMateDao) {
    val projects = dao.activeProjects()
    val archivedProjects = dao.archivedProjects()
    val allMeasurements = dao.allMeasurements()
    val settings = dao.settings()

    fun measurements(projectId: Long) = dao.measurements(projectId)
    fun notes(projectId: Long) = dao.notes(projectId)
    suspend fun project(id: Long) = dao.project(id)
    suspend fun createProject(name: String, folder: String, location: String) =
        dao.insertProject(ProjectEntity(name = name, folder = folder, location = location))

    suspend fun duplicateProject(project: ProjectEntity) =
        dao.insertProject(project.copy(id = 0, name = "${project.name} Copy", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))

    suspend fun archiveProject(id: Long, archived: Boolean) = dao.setArchived(id, archived)
    suspend fun deleteProject(project: ProjectEntity) = dao.deleteProject(project)
    suspend fun saveMeasurement(measurement: MeasurementEntity) = dao.insertMeasurement(measurement)
    suspend fun deleteMeasurement(measurement: MeasurementEntity) = dao.deleteMeasurement(measurement)
    suspend fun saveNote(projectId: Long, body: String) = dao.insertNote(NoteEntity(projectId = projectId, body = body))
    suspend fun setSetting(key: String, value: String) = dao.setSetting(SettingEntity(key, value))
}
