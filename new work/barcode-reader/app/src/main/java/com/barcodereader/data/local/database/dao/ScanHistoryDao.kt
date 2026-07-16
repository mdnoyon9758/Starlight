package com.barcodereader.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Scan History DAO
 *
 * Data Access Object for scan history operations.
 * Provides CRUD operations and queries for scan history.
 */
@Dao
interface ScanHistoryDao {

    // ==================== INSERT ====================

    /**
     * Insert a single scan record
     * If record exists, replace it
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: ScanHistoryEntity): Long

    /**
     * Insert multiple scan records
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scans: List<ScanHistoryEntity>)

    // ==================== UPDATE ====================

    /**
     * Update a scan record
     */
    @Update
    suspend fun update(scan: ScanHistoryEntity)

    /**
     * Update multiple scan records
     */
    @Update
    suspend fun updateAll(scans: List<ScanHistoryEntity>)

    // ==================== DELETE ====================

    /**
     * Delete a scan record
     */
    @Delete
    suspend fun delete(scan: ScanHistoryEntity)

    /**
     * Delete multiple scan records
     */
    @Delete
    suspend fun deleteAll(scans: List<ScanHistoryEntity>)

    /**
     * Delete a scan record by ID
     */
    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Delete multiple scan records by IDs
     */
    @Query("DELETE FROM scan_history WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    /**
     * Delete all scan records
     */
    @Query("DELETE FROM scan_history")
    suspend fun deleteAll()

    /**
     * Delete scans older than a timestamp
     */
    @Query("DELETE FROM scan_history WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * Delete scans in a specific folder
     */
    @Query("DELETE FROM scan_history WHERE folderId = :folderId")
    suspend fun deleteByFolderId(folderId: Long)

    /**
     * Delete scans with a specific category
     */
    @Query("DELETE FROM scan_history WHERE categoryId = :categoryId")
    suspend fun deleteByCategoryId(categoryId: Long)

    // ==================== QUERY - ALL ====================

    /**
     * Get all scan records ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM scan_history ORDER BY isPinned DESC, timestamp DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    /**
     * Get all scan records as a list (for export)
     */
    @Query("SELECT * FROM scan_history ORDER BY isPinned DESC, timestamp DESC")
    suspend fun getAllScansList(): List<ScanHistoryEntity>

    /**
     * Get scan count
     */
    @Query("SELECT COUNT(*) FROM scan_history")
    fun getScanCount(): Flow<Int>

    /**
     * Get scan count as suspend function
     */
    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun getScanCountValue(): Int

    // ==================== QUERY - BY ID ====================

    /**
     * Get a scan record by ID
     */
    @Query("SELECT * FROM scan_history WHERE id = :id")
    suspend fun getById(id: Long): ScanHistoryEntity?

    /**
     * Get a scan record by ID as Flow
     */
    @Query("SELECT * FROM scan_history WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<ScanHistoryEntity?>

    // ==================== QUERY - FAVORITES ====================

    /**
     * Get favorite scan records
     */
    @Query("SELECT * FROM scan_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<ScanHistoryEntity>>

    /**
     * Get favorite count
     */
    @Query("SELECT COUNT(*) FROM scan_history WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    /**
     * Toggle favorite status
     */
    @Query("UPDATE scan_history SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    // ==================== QUERY - PINNED ====================

    /**
     * Get pinned scan records
     */
    @Query("SELECT * FROM scan_history WHERE isPinned = 1 ORDER BY timestamp DESC")
    fun getPinned(): Flow<List<ScanHistoryEntity>>

    /**
     * Toggle pin status
     */
    @Query("UPDATE scan_history SET isPinned = NOT isPinned WHERE id = :id")
    suspend fun togglePin(id: Long)

    // ==================== QUERY - BY TYPE ====================

    /**
     * Get scans by type
     */
    @Query("SELECT * FROM scan_history WHERE type = :type ORDER BY timestamp DESC")
    fun getByType(type: String): Flow<List<ScanHistoryEntity>>

    /**
     * Get distinct scan types with counts
     */
    @Query("SELECT type, COUNT(*) as count FROM scan_history GROUP BY type ORDER BY count DESC")
    fun getTypeStats(): Flow<List<TypeCount>>

    /**
     * Get most common scan type
     */
    @Query("SELECT type FROM scan_history GROUP BY type ORDER BY COUNT(*) DESC LIMIT 1")
    suspend fun getMostCommonType(): String?

    // ==================== QUERY - BY FOLDER ====================

    /**
     * Get scans in a folder
     */
    @Query("SELECT * FROM scan_history WHERE folderId = :folderId ORDER BY timestamp DESC")
    fun getByFolderId(folderId: Long): Flow<List<ScanHistoryEntity>>

    /**
     * Get scans not in any folder
     */
    @Query("SELECT * FROM scan_history WHERE folderId IS NULL ORDER BY timestamp DESC")
    fun getUnfiled(): Flow<List<ScanHistoryEntity>>

    // ==================== QUERY - BY CATEGORY ====================

    /**
     * Get scans in a category
     */
    @Query("SELECT * FROM scan_history WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    fun getByCategoryId(categoryId: Long): Flow<List<ScanHistoryEntity>>

    // ==================== QUERY - SEARCH ====================

    /**
     * Search scans by content
     */
    @Query("""
        SELECT * FROM scan_history 
        WHERE content LIKE '%' || :query || '%' 
        OR type LIKE '%' || :query || '%'
        OR format LIKE '%' || :query || '%'
        OR notes LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun search(query: String): Flow<List<ScanHistoryEntity>>

    /**
     * Search scans by content (suspend)
     */
    @Query("""
        SELECT * FROM scan_history 
        WHERE content LIKE '%' || :query || '%' 
        OR type LIKE '%' || :query || '%'
        OR format LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    suspend fun searchList(query: String): List<ScanHistoryEntity>

    // ==================== QUERY - DUPLICATE DETECTION ====================

    /**
     * Check if a scan with the same content exists
     */
    @Query("SELECT * FROM scan_history WHERE content = :content LIMIT 1")
    suspend fun findByContent(content: String): ScanHistoryEntity?

    /**
     * Get duplicate scans (same content, different IDs)
     */
    @Query("""
        SELECT * FROM scan_history 
        WHERE content IN (
            SELECT content FROM scan_history 
            GROUP BY content 
            HAVING COUNT(*) > 1
        )
        ORDER BY content, timestamp DESC
    """)
    fun getDuplicates(): Flow<List<ScanHistoryEntity>>

    /**
     * Remove duplicates, keeping the most recent
     */
    @Transaction
    suspend fun removeDuplicates() {
        val duplicates = getDuplicatesList()
        val toKeep = mutableSetOf<Long>()
        val toDelete = mutableListOf<Long>()

        // Group by content, keep the most recent
        duplicates.groupBy { it.content }.forEach { (_, scans) ->
            scans.sortedByDescending { it.timestamp }.forEachIndexed { index, scan ->
                if (index == 0) {
                    toKeep.add(scan.id)
                } else {
                    toDelete.add(scan.id)
                }
            }
        }

        if (toDelete.isNotEmpty()) {
            deleteByIds(toDelete)
        }
    }

    /**
     * Get duplicate scans as list
     */
    @Query("""
        SELECT * FROM scan_history 
        WHERE content IN (
            SELECT content FROM scan_history 
            GROUP BY content 
            HAVING COUNT(*) > 1
        )
        ORDER BY content, timestamp DESC
    """)
    suspend fun getDuplicatesList(): List<ScanHistoryEntity>

    // ==================== QUERY - STATISTICS ====================

    /**
     * Get scan count for today
     */
    @Query("SELECT COUNT(*) FROM scan_history WHERE timestamp >= :startOfDay")
    fun getTodayCount(startOfDay: Long): Flow<Int>

    /**
     * Get scan count for a date range
     */
    @Query("SELECT COUNT(*) FROM scan_history WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getCountByDateRange(startDate: Long, endDate: Long): Flow<Int>

    /**
     * Get scan count for a specific date range (suspend)
     */
    @Query("SELECT COUNT(*) FROM scan_history WHERE timestamp BETWEEN :startDate AND :endDate")
    suspend fun getCountByDateRangeValue(startDate: Long, endDate: Long): Int

    /**
     * Get weekly scan counts (last 7 days)
     */
    @Query("""
        SELECT timestamp / 86400000 * 86400000 as dayTimestamp, COUNT(*) as count 
        FROM scan_history 
        WHERE timestamp >= :weekAgo 
        GROUP BY dayTimestamp 
        ORDER BY dayTimestamp
    """)
    fun getWeeklyStats(weekAgo: Long): Flow<List<DailyCount>>

    /**
     * Get monthly scan counts (last 30 days)
     */
    @Query("""
        SELECT timestamp / 86400000 * 86400000 as dayTimestamp, COUNT(*) as count 
        FROM scan_history 
        WHERE timestamp >= :monthAgo 
        GROUP BY dayTimestamp 
        ORDER BY dayTimestamp
    """)
    fun getMonthlyStats(monthAgo: Long): Flow<List<DailyCount>>

    /**
     * Get total scan count
     */
    @Query("SELECT COUNT(*) FROM scan_history")
    fun getTotalCount(): Flow<Int>

    /**
     * Get export count (tracks how many times user exported)
     * This is a placeholder - actual export tracking would need a separate table
     */
    @Query("SELECT COUNT(*) FROM scan_history")
    fun getExportCount(): Flow<Int>

    // ==================== QUERY - TIME-BASED ====================

    /**
     * Get recent scans
     */
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<ScanHistoryEntity>>

    /**
     * Get scans from today
     */
    @Query("SELECT * FROM scan_history WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getTodayScans(startOfDay: Long): Flow<List<ScanHistoryEntity>>

    /**
     * Get scans from this week
     */
    @Query("SELECT * FROM scan_history WHERE timestamp >= :weekAgo ORDER BY timestamp DESC")
    fun getThisWeekScans(weekAgo: Long): Flow<List<ScanHistoryEntity>>

    /**
     * Get scans from this month
     */
    @Query("SELECT * FROM scan_history WHERE timestamp >= :monthAgo ORDER BY timestamp DESC")
    fun getThisMonthScans(monthAgo: Long): Flow<List<ScanHistoryEntity>>

    // ==================== QUERY - BULK OPERATIONS ====================

    /**
     * Set favorite status for multiple items
     */
    @Query("UPDATE scan_history SET isFavorite = :isFavorite WHERE id IN (:ids)")
    suspend fun setFavoriteForIds(ids: List<Long>, isFavorite: Boolean)

    /**
     * Move items to a folder
     */
    @Query("UPDATE scan_history SET folderId = :folderId WHERE id IN (:ids)")
    suspend fun moveToFolder(ids: List<Long>, folderId: Long?)

    /**
     * Set category for multiple items
     */
    @Query("UPDATE scan_history SET categoryId = :categoryId WHERE id IN (:ids)")
    suspend fun setCategoryForIds(ids: List<Long>, categoryId: Long?)

    /**
     * Add tag to multiple items
     */
    @Query("UPDATE scan_history SET notes = :notes WHERE id IN (:ids)")
    suspend fun setNotesForIds(ids: List<Long>, notes: String?)
}

/**
 * Data class for type count results
 */
data class TypeCount(
    val type: String,
    val count: Int
)

/**
 * Data class for daily count results
 */
data class DailyCount(
    val dayTimestamp: Long,
    val count: Int
)