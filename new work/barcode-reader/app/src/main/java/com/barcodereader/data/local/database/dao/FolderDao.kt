package com.barcodereader.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.barcodereader.data.local.database.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Folder DAO
 *
 * Data Access Object for folder operations.
 * Provides CRUD operations for organizing scan history.
 */
@Dao
interface FolderDao {

    // ==================== INSERT ====================

    /**
     * Insert a folder
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: FolderEntity): Long

    /**
     * Insert multiple folders
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(folders: List<FolderEntity>)

    // ==================== UPDATE ====================

    /**
     * Update a folder
     */
    @Update
    suspend fun update(folder: FolderEntity)

    // ==================== DELETE ====================

    /**
     * Delete a folder
     */
    @Delete
    suspend fun delete(folder: FolderEntity)

    /**
     * Delete a folder by ID
     */
    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Delete all folders
     */
    @Query("DELETE FROM folders")
    suspend fun deleteAll()

    // ==================== QUERY ====================

    /**
     * Get all folders ordered by name
     */
    @Query("SELECT * FROM folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    /**
     * Get all folders as list
     */
    @Query("SELECT * FROM folders ORDER BY name ASC")
    suspend fun getAllFoldersList(): List<FolderEntity>

    /**
     * Get a folder by ID
     */
    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getById(id: Long): FolderEntity?

    /**
     * Get a folder by ID as Flow
     */
    @Query("SELECT * FROM folders WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<FolderEntity?>

    /**
     * Search folders by name
     */
    @Query("SELECT * FROM folders WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<FolderEntity>>

    /**
     * Get folder count
     */
    @Query("SELECT COUNT(*) FROM folders")
    fun getFolderCount(): Flow<Int>

    /**
     * Get folder with scan count
     */
    @Query("""
        SELECT f.*, COUNT(s.id) as scanCount 
        FROM folders f 
        LEFT JOIN scan_history s ON f.id = s.folderId 
        GROUP BY f.id 
        ORDER BY f.name ASC
    """)
    fun getFoldersWithCount(): Flow<List<FolderWithCount>>
}

/**
 * Data class for folder with scan count
 */
data class FolderWithCount(
    val id: Long,
    val name: String,
    val color: Long,
    val icon: String,
    val createdAt: Long,
    val updatedAt: Long,
    val scanCount: Int
)