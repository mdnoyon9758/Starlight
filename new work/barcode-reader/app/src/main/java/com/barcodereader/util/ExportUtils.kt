package com.barcodereader.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.barcodereader.data.ScanHistory
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {
    
    enum class ExportFormat {
        CSV, JSON
    }
    
    fun exportToCsv(context: Context, history: List<ScanHistory>): Uri? {
        return try {
            val file = createExportFile(context, "scan_history.csv")
            val writer = FileWriter(file)
            
            // Write header
            writer.append("ID,Content,Type,Format,Timestamp,Date,Favorite\n")
            
            // Write data
            history.forEach { entry ->
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(entry.timestamp))
                writer.append("${entry.id},\"${escapeCsv(entry.content)}\",${entry.type},${entry.format},$entry.timestamp,$date,${entry.isFavorite}\n")
            }
            
            writer.flush()
            writer.close()
            
            getFileUri(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun exportToJson(context: Context, history: List<ScanHistory>): Uri? {
        return try {
            val file = createExportFile(context, "scan_history.json")
            val jsonArray = JSONArray()
            
            history.forEach { entry ->
                val jsonObject = JSONObject().apply {
                    put("id", entry.id)
                    put("content", entry.content)
                    put("type", entry.type)
                    put("format", entry.format)
                    put("timestamp", entry.timestamp)
                    put("date", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(entry.timestamp)))
                    put("favorite", entry.isFavorite)
                }
                jsonArray.put(jsonObject)
            }
            
            val rootObject = JSONObject().apply {
                put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date()))
                put("totalScans", history.size)
                put("scans", jsonArray)
            }
            
            val writer = FileWriter(file)
            writer.write(rootObject.toString(2))
            writer.flush()
            writer.close()
            
            getFileUri(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareExport(context: Context, uri: Uri, format: ExportFormat) {
        val mimeType = when (format) {
            ExportFormat.CSV -> "text/csv"
            ExportFormat.JSON -> "application/json"
        }
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Scan History"))
    }
    
    private fun createExportFile(context: Context, fileName: String): File {
        val exportDir = File(context.cacheDir, "exports")
        exportDir.mkdirs()
        return File(exportDir, fileName)
    }
    
    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }
    
    fun filterByDateRange(
        history: List<ScanHistory>,
        startDate: Long?,
        endDate: Long?
    ): List<ScanHistory> {
        return history.filter { entry ->
            val afterStart = startDate == null || entry.timestamp >= startDate
            val beforeEnd = endDate == null || entry.timestamp <= endDate
            afterStart && beforeEnd
        }
    }
}
