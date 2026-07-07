package com.measuremate.features.manual.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.measuremate.MeasureMateViewModel
import com.measuremate.core.ui.SectionCard
import com.measuremate.core.utils.MeasureUnit
import com.measuremate.core.utils.manualResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualMeasureScreen(viewModel: MeasureMateViewModel) {
    val projects by viewModel.projects.collectAsState()
    var title by remember { mutableStateOf("Manual measurement") }
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var thickness by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var unit by remember { mutableStateOf(MeasureUnit.Meter) }
    val result = manualResult(length.toDoubleOrNull() ?: 0.0, width.toDoubleOrNull() ?: 0.0, height.toDoubleOrNull() ?: 0.0)
    val projectId = projects.firstOrNull()?.id ?: 0L

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Manual Measure", style = MaterialTheme.typography.headlineSmall)
        SectionCard {
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumberField("Length", length, { length = it }, Modifier.weight(1f))
                NumberField("Width", width, { width = it }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumberField("Height", height, { height = it }, Modifier.weight(1f))
                NumberField("Thickness", thickness, { thickness = it }, Modifier.weight(1f))
            }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = unit.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    MeasureUnit.entries.forEach {
                        DropdownMenuItem(text = { Text(it.label) }, onClick = { unit = it; expanded = false })
                    }
                }
            }
            Text("Area: ${"%.2f".format(result.area)} ${unit.label}2")
            Text("Perimeter: ${"%.2f".format(result.perimeter)} ${unit.label}")
            Text("Volume: ${"%.2f".format(result.volume)} ${unit.label}3")
            Text("Surface Area: ${"%.2f".format(result.surfaceArea)} ${unit.label}2")
            Button(
                enabled = projectId != 0L,
                onClick = {
                    viewModel.saveManual(
                        projectId,
                        title,
                        length.toDoubleOrNull() ?: 0.0,
                        width.toDoubleOrNull() ?: 0.0,
                        height.toDoubleOrNull() ?: 0.0,
                        thickness.toDoubleOrNull() ?: 0.0,
                        unit.label
                    )
                }
            ) { Text(if (projectId == 0L) "Create a project first" else "Save to Project") }
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}
