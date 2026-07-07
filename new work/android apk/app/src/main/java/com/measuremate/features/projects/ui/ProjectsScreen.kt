package com.measuremate.features.projects.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.measuremate.MeasureMateViewModel
import com.measuremate.core.ui.SectionCard

@Composable
fun ProjectsScreen(viewModel: MeasureMateViewModel) {
    val projects by viewModel.projects.collectAsState()
    val measurements by viewModel.measurements.collectAsState()
    var name by remember { mutableStateOf("") }
    var folder by remember { mutableStateOf("General") }
    var location by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    val filtered = projects.filter { it.name.contains(search, ignoreCase = true) || it.folder.contains(search, ignoreCase = true) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("Projects", style = MaterialTheme.typography.headlineSmall)
            Text("Create folders, store measurements, duplicate, search, and archive.")
        }
        item {
            SectionCard {
                OutlinedTextField(name, { name = it }, label = { Text("Project name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(folder, { folder = it }, label = { Text("Folder") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(location, { location = it }, label = { Text("Location optional") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { viewModel.createProject(name, folder, location); name = ""; location = "" }) { Text("Create Project") }
            }
        }
        item {
            OutlinedTextField(search, { search = it }, label = { Text("Search") }, modifier = Modifier.fillMaxWidth())
        }
        items(filtered) { project ->
            SectionCard {
                Text(project.name, style = MaterialTheme.typography.titleMedium)
                Text("Folder: ${project.folder}")
                if (project.location.isNotBlank()) Text("Location: ${project.location}")
                Text("Measurements: ${measurements.count { it.projectId == project.id }}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.duplicateFirstProject(project.id) }) { Text("Duplicate") }
                    Button(onClick = { viewModel.archiveProject(project.id) }) { Text("Archive") }
                }
            }
        }
    }
}
