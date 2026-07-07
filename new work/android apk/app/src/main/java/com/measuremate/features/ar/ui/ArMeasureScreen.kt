package com.measuremate.features.ar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.measuremate.MeasureMateViewModel
import com.measuremate.core.sensors.CapabilityChecker
import com.measuremate.core.ui.SectionCard

@Composable
fun ArMeasureScreen(viewModel: MeasureMateViewModel) {
    val context = LocalContext.current
    val arSupported = remember { CapabilityChecker(context).isArSupported() }
    val projects by viewModel.projects.collectAsState()
    var distance by remember { mutableStateOf("") }
    val projectId = projects.firstOrNull()?.id ?: 0L

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AR Measure", style = MaterialTheme.typography.headlineSmall)
        SectionCard {
            if (arSupported) {
                Text("Basic AR distance measurement", style = MaterialTheme.typography.titleMedium)
                Text("Camera ARCore integration is available on this device. The first build stores distance estimates and keeps manual fallback available.")
                LinearProgressIndicator(progress = { 0.35f }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Measured distance estimate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(enabled = projectId != 0L, onClick = { viewModel.saveArEstimate(projectId, distance.toDoubleOrNull() ?: 0.0, "meter") }) {
                        Text("Save")
                    }
                    Button(onClick = { distance = "" }) { Text("Reset") }
                }
            } else {
                Text("ARCore unavailable", style = MaterialTheme.typography.titleMedium)
                Text("AR measurement is not supported on this device. Manual measurement, calculators, level tools, projects, and reports remain usable offline.")
            }
        }
        SectionCard {
            Text("Planned AR tools", style = MaterialTheme.typography.titleMedium)
            Text("Distance, height, width, simple area, multiple points, screenshots, and reset workflow are reserved in this module.")
        }
    }
}
