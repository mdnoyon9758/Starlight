package com.barcodereader.data.repository

import com.barcodereader.data.local.database.dao.ScanHistoryDao
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.barcodereader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scan Repository
 *
 * Handles all scan history operations.
 * Business domain: scan history management.
 */
@Singleton
class ScanRepository @Inject constructor(
    private val scanHistoryDao: ScanHistoryDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Get all scans ordered by timestamp
     */
    fun getAllScans(): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getAllScans()
    }

    /**
     * Get scan by ID
     */
    suspend fun getScanById(id: Long): ScanHistoryEntity? {
        return withContext(ioDispatcher) {
            scanHistoryDao.getById(id)
        }
    }

    /**
     * Insert a new scan
     */
    suspend fun insertScan(scan: ScanHistoryEntity): Long {
        return withContext(ioDispatcher) {
            scanHistoryDao.insert(scan)
        }
    }

    /**
     * Update a scan
     */
    suspend fun updateScan(scan: ScanHistoryEntity) {
        withContext(ioDispatcher) {
            scanHistoryDao.update(scan)
        }
    }

    /**
     * Delete a scan
     */
    suspend fun deleteScan(scan: ScanHistoryEntity) {
        withContext(ioDispatcher) {
            scanHistoryDao.delete(scan)
        }
    }

    /**
     * Delete scan by ID
     */
    suspend fun deleteScanById(id: Long) {
        withContext(ioDispatcher) {
            scanHistoryDao.deleteById(id)
        }
    }

    /**
     * Delete multiple scans
     */
    suspend fun deleteScansByIds(ids: List<Long>) {
        withContext(ioDispatcher) {
            scanHistoryDao.deleteByIds(ids)
        }
    }

    /**
     * Delete all scans
     */
    suspend fun deleteAllScans() {
        withContext(ioDispatcher) {
            scanHistoryDao.deleteAll()
        }
    }

    /**
     * Get favorites
     */
    fun getFavorites(): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getFavorites()
    }

    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(id: Long) {
        withContext(ioDispatcher) {
            scanHistoryDao.toggleFavorite(id)
        }
    }

    /**
     * Toggle pin status
     */
    suspend fun togglePin(id: Long) {
        withContext(ioDispatcher) {
            scanHistoryDao.togglePin(id)
        }
    }

    /**
     * Search scans
     */
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.search(query)
    }

    /**
     * Get scans by type
     */
    fun getScansByType(type: String): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getByType(type)
    }

    /**
     * Get scans by folder
     */
    fun getScansByFolder(folderId: Long): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getByFolderId(folderId)
    }

    /**
     * Get scans by category
     */
    fun getScansByCategory(categoryId: Long): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getByCategoryId(categoryId)
    }

    /**
     * Get unfiled scans
     */
    fun getUnfiledScans(): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getUnfiled()
    }

    /**
     * Get scan count
     */
    fun getScanCount(): Flow<Int> {
        return scanHistoryDao.getScanCount()
    }

    /**
     * Get favorite count
     */
    fun getFavoriteCount(): Flow<Int> {
        return scanHistoryDao.getFavoriteCount()
    }

    /**
     * Find duplicate by content
     */
    suspend fun findByContent(content: String): ScanHistoryEntity? {
        return withContext(ioDispatcher) {
            scanHistoryDao.findByContent(content)
        }
    }

    /**
     * Get duplicate scans
     */
    fun getDuplicates(): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getDuplicates()
    }

    /**
     * Remove duplicates
     */
    suspend fun removeDuplicates() {
        withContext(ioDispatcher) {
            scanHistoryDao.removeDuplicates()
        }
    }

    /**
     * Get type statistics
     */
    fun getTypeStats(): Flow<List<com.barcodereader.data.local.database.dao.TypeCount>> {
        return scanHistoryDao.getTypeStats()
    }

    /**
     * Get today's scan count
     */
    fun getTodayCount(startOfDay: Long): Flow<Int> {
        return scanHistoryDao.getTodayCount(startOfDay)
    }

    /**
     * Get weekly stats
     */
    fun getWeeklyStats(weekAgo: Long): Flow<List<com.barcodereader.data.local.database.dao.DailyCount>> {
        return scanHistoryDao.getWeeklyStats(weekAgo)
    }

    /**
     * Get monthly stats
     */
    fun getMonthlyStats(monthAgo: Long): Flow<List<com.barcodereader.data.local.database.dao.DailyCount>> {
        return scanHistoryDao.getMonthlyStats(monthAgo)
    }

    /**
     * Move scans to folder
     */
    suspend fun moveToFolder(ids: List<Long>, folderId: Long?) {
        withContext(ioDispatcher) {
            scanHistoryDao.moveToFolder(ids, folderId)
        }
    }

    /**
     * Set category for scans
     */
    suspend fun setCategoryForIds(ids: List<Long>, categoryId: Long?) {
        withContext(ioDispatcher) {
            scanHistoryDao.setCategoryForIds(ids, categoryId)
        }
    }

    /**
     * Set favorite for multiple scans
     */
    suspend fun setFavoriteForIds(ids: List<Long>, isFavorite: Boolean) {
        withContext(ioDispatcher) {
            scanHistoryDao.setFavoriteForIds(ids, isFavorite)
        }
    }

    /**
     * Get all scans as list (for export)
     */
    suspend fun getAllScansList(): List<ScanHistoryEntity> {
        return withContext(ioDispatcher) {
            scanHistoryDao.getAllScansList()
        }
    }
}