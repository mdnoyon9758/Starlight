package com.measuremate.features.reports.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.measuremate.MeasureMateViewModel
import com.measuremate.core.ui.SectionCard
import com.measuremate.core.utils.PdfReportWriter

@Composable
fun ReportsScreen(viewModel: MeasureMateViewModel) {
    val context = LocalContext.current
    val projects by viewModel.projects.collectAsState()
    val measurements by viewModel.measurements.collectAsState()
    val project = projects.firstOrNull()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Reports", style = MaterialTheme.typography.headlineSmall)
        SectionCard {
            Text("PDF Export", style = MaterialTheme.typography.titleMedium)
            Text("Exports a local offline PDF with project name, folder, location, measurements, and notes.")
            Button(enabled = project != null, onClick = {
                if (project != null) {
                    val file = PdfReportWriter.writeProjectReport(context, project, measurements.filter { it.projectId == project.id }, emptyList())
                    val uri = PdfReportWriter.uriFor(context, file)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share MeasureMate report"))
                }
            }) {
                Text(if (project == null) "Create a project first" else "Export Latest Project")
            }
        }
        SectionCard {
            Text("Report content", style = MaterialTheme.typography.titleMedium)
            Text("Company logo, images, print templates, and richer notes are reserved in the report module and can be refined after real-device testing.")
        }
    }
}
