package com.barcodereader.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.barcodereader.data.local.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Tag DAO
 *
 * Data Access Object for tag operations.
 * Provides CRUD operations for labeling scan history.
 */
@Dao
interface TagDao {

    // ==================== INSERT ====================

    /**
     * Insert a tag
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity): Long

    /**
     * Insert multiple tags
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags: List<TagEntity>)

    // ==================== UPDATE ====================

    /**
     * Update a tag
     */
    @Update
    suspend fun update(tag: TagEntity)

    // ==================== DELETE ====================

    /**
     * Delete a tag
     */
    @Delete
    suspend fun delete(tag: TagEntity)

    /**
     * Delete a tag by ID
     */
    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Delete all tags
     */
    @Query("DELETE FROM tags")
    suspend fun deleteAll()

    // ==================== QUERY ====================

    /**
     * Get all tags ordered by name
     */
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    /**
     * Get all tags as list
     */
    @Query("SELECT * FROM tags ORDER BY name ASC")
    suspend fun getAllTagsList(): List<TagEntity>

    /**
     * Get a tag by ID
     */
    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getById(id: Long): TagEntity?

    /**
     * Get a tag by ID as Flow
     */
    @Query("SELECT * FROM tags WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<TagEntity?>

    /**
     * Search tags by name
     */
    @Query("SELECT * FROM tags WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<TagEntity>>

    /**
     * Get tag count
     */
    @Query("SELECT COUNT(*) FROM tags")
    fun getTagCount(): Flow<Int>

    /**
     * Get tags for a specific scan
     */
    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN scan_tag_cross_ref ref ON t.id = ref.tagId
        WHERE ref.scanId = :scanId
        ORDER BY t.name ASC
    """)
    fun getTagsForScan(scanId: Long): Flow<List<TagEntity>>

    /**
     * Get tags for a specific scan as list
     */
    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN scan_tag_cross_ref ref ON t.id = ref.tagId
        WHERE ref.scanId = :scanId
        ORDER BY t.name ASC
    """)
    suspend fun getTagsForScanList(scanId: Long): List<TagEntity>

    /**
     * Get scans for a specific tag
     */
    @Query("""
        SELECT s.* FROM scan_history s
        INNER JOIN scan_tag_cross_ref ref ON s.id = ref.scanId
        WHERE ref.tagId = :tagId
        ORDER BY s.timestamp DESC
    """)
    fun getScansForTag(tagId: Long): Flow<List<com.barcodereader.data.local.database.entity.ScanHistoryEntity>>

    /**
     * Add tag to scan
     */
    @Query("INSERT OR IGNORE INTO scan_tag_cross_ref (scanId, tagId) VALUES (:scanId, :tagId)")
    suspend fun addTagToScan(scanId: Long, tagId: Long)

    /**
     * Remove tag from scan
     */
    @Query("DELETE FROM scan_tag_cross_ref WHERE scanId = :scanId AND tagId = :tagId")
    suspend fun removeTagFromScan(scanId: Long, tagId: Long)

    /**
     * Remove all tags from a scan
     */
    @Query("DELETE FROM scan_tag_cross_ref WHERE scanId = :scanId")
    suspend fun removeAllTagsFromScan(scanId: Long)

    /**
     * Check if scan has tag
     */
    @Query("SELECT EXISTS(SELECT 1 FROM scan_tag_cross_ref WHERE scanId = :scanId AND tagId = :tagId)")
    suspend fun hasTag(scanId: Long, tagId: Long): Boolean
}