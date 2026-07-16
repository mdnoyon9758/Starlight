package com.barcodereader.domain.usecase

import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.barcodereader.data.repository.ScanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Scan Use Cases
 *
 * Grouped use cases for scan history operations.
 */
class ScanUseCases @Inject constructor(
    private val scanRepository: ScanRepository
) {
    /**
     * Get all scans
     */
    fun getAllScans(): Flow<List<ScanHistoryEntity>> {
        return scanRepository.getAllScans()
    }

    /**
     * Get scan by ID
     */
    suspend fun getScanById(id: Long): ScanHistoryEntity? {
        return scanRepository.getScanById(id)
    }

    /**
     * Save a new scan
     */
    suspend fun saveScan(scan: ScanHistoryEntity): Long {
        return scanRepository.insertScan(scan)
    }

    /**
     * Update a scan
     */
    suspend fun updateScan(scan: ScanHistoryEntity) {
        scanRepository.updateScan(scan)
    }

    /**
     * Delete a scan
     */
    suspend fun deleteScan(id: Long) {
        scanRepository.deleteScanById(id)
    }

    /**
     * Delete multiple scans
     */
    suspend fun deleteScans(ids: List<Long>) {
        scanRepository.deleteScansByIds(ids)
    }

    /**
     * Delete all scans
     */
    suspend fun deleteAllScans() {
        scanRepository.deleteAllScans()
    }

    /**
     * Toggle favorite
     */
    suspend fun toggleFavorite(id: Long) {
        scanRepository.toggleFavorite(id)
    }

    /**
     * Toggle pin
     */
    suspend fun togglePin(id: Long) {
        scanRepository.togglePin(id)
    }

    /**
     * Search scans
     */
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>> {
        return scanRepository.searchScans(query)
    }

    /**
     * Get scans by type
     */
    fun getScansByType(type: String): Flow<List<ScanHistoryEntity>> {
        return scanRepository.getScansByType(type)
    }

    /**
     * Get scans by folder
     */
    fun getScansByFolder(folderId: Long): Flow<List<ScanHistoryEntity>> {
        return scanRepository.getScansByFolder(folderId)
    }

    /**
     * Get scans by category
     */
    fun getScansByCategory(categoryId: Long): Flow<List<ScanHistoryEntity>> {
        return scanRepository.getScansByCategory(categoryId)
    }

    /**
     * Get favorites
     */
    fun getFavorites(): Flow<List<ScanHistoryEntity>> {
        return scanRepository.getFavorites()
    }

    /**
     * Get scan count
     */
    fun getScanCount(): Flow<Int> {
        return scanRepository.getScanCount()
    }

    /**
     * Get favorite count
     */
    fun getFavoriteCount(): Flow<Int> {
        return scanRepository.getFavoriteCount()
    }

    /**
     * Find by content
     */
    suspend fun findByContent(content: String): ScanHistoryEntity? {
        return scanRepository.findByContent(content)
    }

    /**
     * Get duplicates
     */
    fun getDuplicates(): Flow<List<ScanHistoryEntity>> {
        return scanRepository.getDuplicates()
    }

    /**
     * Remove duplicates
     */
    suspend fun removeDuplicates() {
        scanRepository.removeDuplicates()
    }

    /**
     * Get type stats
     */
    fun getTypeStats(): Flow<List<com.barcodereader.data.local.database.dao.TypeCount>> {
        return scanRepository.getTypeStats()
    }

    /**
     * Get today count
     */
    fun getTodayCount(startOfDay: Long): Flow<Int> {
        return scanRepository.getTodayCount(startOfDay)
    }

    /**
     * Get weekly stats
     */
    fun getWeeklyStats(weekAgo: Long): Flow<List<com.barcodereader.data.local.database.dao.DailyCount>> {
        return scanRepository.getWeeklyStats(weekAgo)
    }

    /**
     * Get monthly stats
     */
    fun getMonthlyStats(monthAgo: Long): Flow<List<com.barcodereader.data.local.database.dao.DailyCount>> {
        return scanRepository.getMonthlyStats(monthAgo)
    }

    /**
     * Move to folder
     */
    suspend fun moveToFolder(ids: List<Long>, folderId: Long?) {
        scanRepository.moveToFolder(ids, folderId)
    }

    /**
     * Set category
     */
    suspend fun setCategory(ids: List<Long>, categoryId: Long?) {
        scanRepository.setCategoryForIds(ids, categoryId)
    }

    /**
     * Set favorite for multiple
     */
    suspend fun setFavoriteForIds(ids: List<Long>, isFavorite: Boolean) {
        scanRepository.setFavoriteForIds(ids, isFavorite)
    }

    /**
     * Get all scans as list
     */
    suspend fun getAllScansList(): List<ScanHistoryEntity> {
        return scanRepository.getAllScansList()
    }
}