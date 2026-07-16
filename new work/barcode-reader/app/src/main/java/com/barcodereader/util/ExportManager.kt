package com.barcodereader.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

/**
 * Export Manager
 *
 * Handles all export operations for the app.
 */
class ExportManager @Inject constructor() {

    /**
     * Export to CSV
     */
    fun exportToCsv(context: Context, scans: List<ScanHistoryEntity>): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()

            val file = File(exportDir, "scan_history.csv")
            file.bufferedWriter().use { writer ->
                writer.appendLine("ID,Content,Type,Format,Timestamp,Favorite,Notes")
                scans.forEach { scan ->
                    val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date(scan.timestamp))
                    writer.appendLine("${scan.id},\"${scan.content.escapeCsv()}\",${scan.type},${scan.format},$date,${scan.isFavorite},\"${(scan.notes ?: "").escapeCsv()}\"")
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Export to JSON
     */
    fun exportToJson(context: Context, scans: List<ScanHistoryEntity>): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()

            val file = File(exportDir, "scan_history.json")
            val gson = Gson()
            file.writeText(gson.toJson(scans))
            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Export to PDF
     */
    fun exportToPdf(context: Context, scans: List<ScanHistoryEntity>): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()

            val file = File(exportDir, "scan_history.pdf")

            // Simple PDF generation using Android's PdfDocument
            val document = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)

            val canvas = page.canvas
            val paint = android.graphics.Paint().apply {
                textSize = 12f
                color = android.graphics.Color.BLACK
            }

            var y = 40f
            canvas.drawText("Scan History Export", 40f, y, paint.apply { textSize = 18f; isFakeBoldText = true })
            y += 40f

            scans.forEach { scan ->
                canvas.drawText("Content: ${scan.content.take(50)}", 40f, y, paint.apply { textSize = 10f })
                y += 20f
                canvas.drawText("Type: ${scan.type} | Format: ${scan.format}", 40f, y, paint)
                y += 30f

                if (y > 800) {
                    document.finishPage(page)
                    val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                    val newPage = document.startPage(newPageInfo)
                    y = 40f
                }
            }

            document.finishPage(page)
            file.outputStream().use { document.writeTo(it) }
            document.close()

            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Export bitmap to PNG
     */
    fun exportToPng(context: Context, bitmap: Bitmap, filename: String): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()

            val file = File(exportDir, "$filename.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Export bitmap to SVG
     */
    fun exportToSvg(context: Context, bitmap: Bitmap, filename: String): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()

            val file = File(exportDir, "$filename.svg")
            val width = bitmap.width
            val height = bitmap.height

            val svg = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                appendLine("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"$width\" height=\"$height\" viewBox=\"0 0 $width $height\">")
                appendLine("<image href=\"data:image/png;base64,${android.util.Base64.encodeToString(bitmapToByteArray(bitmap), android.util.Base64.NO_WRAP)}\" width=\"$width\" height=\"$height\"/>")
                appendLine("</svg>")
            }

            file.writeText(svg)
            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Export to ZIP
     */
    fun exportToZip(context: Context, files: List<File>, filename: String): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()

            val zipFile = File(exportDir, "$filename.zip")
            ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                files.forEach { file ->
                    ZipEntry(file.name).let { entry ->
                        zipOut.putNextEntry(entry)
                        file.inputStream().use { input ->
                            input.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                    }
                }
            }
            zipFile
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Import from ZIP
     */
    fun importFromZip(context: Context, zipFile: File): List<File>? {
        return try {
            val importDir = File(context.cacheDir, "imports")
            importDir.mkdirs()

            val importedFiles = mutableListOf<File>()

            ZipInputStream(zipFile.inputStream()).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val outFile = File(importDir, entry.name)
                    outFile.outputStream().use { out ->
                        zipIn.copyTo(out)
                    }
                    importedFiles.add(outFile)
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            importedFiles
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Share file
     */
    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = getMimeType(file)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share"))
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "csv" -> "text/csv"
            "json" -> "application/json"
            "pdf" -> "application/pdf"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "png" -> "image/png"
            "svg" -> "image/svg+xml"
            "zip" -> "application/zip"
            else -> "application/octet-stream"
        }
    }

    private fun String.escapeCsv(): String {
        return replace("\"", "\"\"")
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}