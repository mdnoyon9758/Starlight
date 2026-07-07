package com.measuremate.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.measuremate.MeasureMateViewModel
import com.measuremate.core.ui.SectionCard

@Composable
fun SettingsScreen(viewModel: MeasureMateViewModel, onAbout: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    fun value(key: String, fallback: String) = settings.firstOrNull { it.key == key }?.value ?: fallback
    val theme = value("theme", "System")
    val unit = value("unit", "meter")
    val precision = value("precision", "2")
    val sound = value("sound", "true") == "true"
    val haptic = value("haptic", "true") == "true"

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        SectionCard {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Dark", "Light", "System").forEach {
                    FilterChip(selected = theme == it, onClick = { viewModel.setSetting("theme", it) }, label = { Text(it) })
                }
            }
        }
        SectionCard {
            Text("Measurement Units", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("mm", "cm", "meter", "inch", "feet", "yard").forEach {
                    FilterChip(selected = unit == it, onClick = { viewModel.setSetting("unit", it) }, label = { Text(it) })
                }
            }
        }
        SectionCard {
            Text("Precision", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("0", "1", "2", "3").forEach {
                    FilterChip(selected = precision == it, onClick = { viewModel.setSetting("precision", it) }, label = { Text(it) })
                }
            }
            ToggleRow("Grid", value("grid", "true") == "true") { viewModel.setSetting("grid", it.toString()) }
            ToggleRow("Sound", sound) { viewModel.setSetting("sound", it.toString()) }
            ToggleRow("Haptic Feedback", haptic) { viewModel.setSetting("haptic", it.toString()) }
        }
        SectionCard {
            Text("Backup and Restore", style = MaterialTheme.typography.titleMedium)
            Text("Local database export and restore are reserved for the next refinement pass.")
            Button(onClick = onAbout) { Text("About") }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
