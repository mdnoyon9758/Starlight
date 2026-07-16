package com.barcodereader.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.barcodereader.util.ExportManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Export Use Cases
 *
 * Grouped use cases for export operations.
 */
class ExportUseCases @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportManager: ExportManager
) {

    /**
     * Export to CSV
     */
    suspend fun exportToCsv(scans: List<ScanHistoryEntity>): File? {
        return withContext(Dispatchers.IO) {
            exportManager.exportToCsv(context, scans)
        }
    }

    /**
     * Export to JSON
     */
    suspend fun exportToJson(scans: List<ScanHistoryEntity>): File? {
        return withContext(Dispatchers.IO) {
            exportManager.exportToJson(context, scans)
        }
    }

    /**
     * Export to PDF
     */
    suspend fun exportToPdf(scans: List<ScanHistoryEntity>): File? {
        return withContext(Dispatchers.IO) {
            exportManager.exportToPdf(context, scans)
        }
    }

    /**
     * Export bitmap to PNG
     */
    suspend fun exportToPng(bitmap: Bitmap, filename: String): File? {
        return withContext(Dispatchers.IO) {
            exportManager.exportToPng(context, bitmap, filename)
        }
    }

    /**
     * Export bitmap to SVG
     */
    suspend fun exportToSvg(bitmap: Bitmap, filename: String): File? {
        return withContext(Dispatchers.IO) {
            exportManager.exportToSvg(context, bitmap, filename)
        }
    }

    /**
     * Export to ZIP (bundle multiple exports)
     */
    suspend fun exportToZip(files: List<File>, filename: String): File? {
        return withContext(Dispatchers.IO) {
            exportManager.exportToZip(context, files, filename)
        }
    }

    /**
     * Import from ZIP
     */
    suspend fun importFromZip(zipFile: File): List<File>? {
        return withContext(Dispatchers.IO) {
            exportManager.importFromZip(context, zipFile)
        }
    }

    /**
     * Share file
     */
    fun shareFile(file: File) {
        exportManager.shareFile(context, file)
    }
}