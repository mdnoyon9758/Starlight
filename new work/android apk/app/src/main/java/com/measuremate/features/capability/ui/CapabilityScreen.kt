package com.measuremate.features.capability.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.measuremate.core.sensors.CapabilityChecker
import com.measuremate.core.ui.SectionCard
import com.measuremate.core.ui.StatusRow

@Composable
fun CapabilityScreen() {
    val context = LocalContext.current
    val items = remember { CapabilityChecker(context).scan() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Device Capability", style = MaterialTheme.typography.headlineSmall)
        Text("Unsupported hardware is handled with fallback tools where possible.", style = MaterialTheme.typography.bodyMedium)
        LazyColumn(Modifier.padding(top = 16.dp)) {
            item {
                SectionCard {
                    Text("Hardware and software status", style = MaterialTheme.typography.titleMedium)
                    items.forEach { StatusRow(it.name, it.status, it.detail) }
                }
            }
        }
    }
}
