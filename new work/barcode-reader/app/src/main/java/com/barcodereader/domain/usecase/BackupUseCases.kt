package com.barcodereader.domain.usecase

import android.content.Context
import com.barcodereader.data.local.database.AppDatabase
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Backup Use Cases
 *
 * Grouped use cases for backup/restore operations.
 */
class BackupUseCases @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {

    private val gson = Gson()

    /**
     * Create backup of scan history
     */
    suspend fun createBackup(): File? {
        return withContext(Dispatchers.IO) {
            try {
                val scans = database.scanHistoryDao().getAllScansList()
                val json = gson.toJson(scans)

                val backupDir = File(context.filesDir, "backups")
                backupDir.mkdirs()

                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val backupFile = File(backupDir, "backup_$timestamp.json")

                backupFile.writeText(json)
                backupFile
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Restore from backup
     */
    suspend fun restoreBackup(backupFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = backupFile.readText()
                val type = object : TypeToken<List<ScanHistoryEntity>>() {}.type
                val scans: List<ScanHistoryEntity> = gson.fromJson(json, type)

                database.scanHistoryDao().insertAll(scans)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Get backup files
     */
    fun getBackupFiles(): List<File> {
        val backupDir = File(context.filesDir, "backups")
        return if (backupDir.exists()) {
            backupDir.listFiles()?.filter { it.extension == "json" }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Delete backup file
     */
    fun deleteBackup(file: File): Boolean {
        return file.delete()
    }

    /**
     * Get backup size
     */
    fun getBackupSize(file: File): Long {
        return file.length()
    }
}