package com.measuremate.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.measuremate.data.models.MeasurementEntity
import com.measuremate.data.models.NoteEntity
import com.measuremate.data.models.PhotoEntity
import com.measuremate.data.models.ProjectEntity
import com.measuremate.data.models.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasureMateDao {
    @Query("SELECT * FROM projects WHERE archived = 0 ORDER BY updatedAt DESC")
    fun activeProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE archived = 1 ORDER BY updatedAt DESC")
    fun archivedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun project(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("UPDATE projects SET archived = :archived, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM measurements WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun measurements(projectId: Long): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements ORDER BY createdAt DESC")
    fun allMeasurements(): Flow<List<MeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity): Long

    @Delete
    suspend fun deleteMeasurement(measurement: MeasurementEntity)

    @Query("SELECT * FROM notes WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun notes(projectId: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("SELECT * FROM photos WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun photos(projectId: Long): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Query("SELECT * FROM settings")
    fun settings(): Flow<List<SettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}
