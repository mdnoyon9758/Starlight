package com.measuremate.core.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.measuremate.data.models.MeasurementEntity
import com.measuremate.data.models.ProjectEntity
import java.io.File
import java.io.FileOutputStream

object PdfReportWriter {
    fun writeProjectReport(
        context: Context,
        project: ProjectEntity,
        measurements: List<MeasurementEntity>,
        notes: List<String>
    ): File {
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 12f }
        var y = 48f
        canvas.drawText("MeasureMate Report", 40f, y, titlePaint)
        y += 28f
        canvas.drawText("Project: ${project.name}", 40f, y, bodyPaint)
        y += 20f
        canvas.drawText("Folder: ${project.folder}", 40f, y, bodyPaint)
        if (project.location.isNotBlank()) {
            y += 20f
            canvas.drawText("Location: ${project.location}", 40f, y, bodyPaint)
        }
        y += 32f
        canvas.drawText("Measurements", 40f, y, titlePaint.apply { textSize = 18f })
        y += 24f
        measurements.take(24).forEach {
            canvas.drawText("${it.title}: L ${it.length} ${it.unit}, W ${it.width}, H ${it.height}, Area ${it.area}, Volume ${it.volume}", 40f, y, bodyPaint)
            y += 18f
        }
        if (notes.isNotEmpty()) {
            y += 20f
            canvas.drawText("Notes", 40f, y, titlePaint)
            y += 24f
            notes.take(8).forEach {
                canvas.drawText(it.take(90), 40f, y, bodyPaint)
                y += 18f
            }
        }
        document.finishPage(page)
        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(dir, "measuremate-report-${project.id}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }

    fun uriFor(context: Context, file: File) =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
