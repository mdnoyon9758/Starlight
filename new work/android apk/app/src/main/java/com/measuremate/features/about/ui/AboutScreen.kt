package com.measuremate.features.about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.measuremate.core.ui.SectionCard

@Composable
fun AboutScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("About MeasureMate", style = MaterialTheme.typography.headlineSmall)
        SectionCard {
            Text("Version", style = MaterialTheme.typography.titleMedium)
            Text("1.0")
        }
        SectionCard {
            Text("Privacy", style = MaterialTheme.typography.titleMedium)
            Text("MeasureMate is offline-first. Projects, measurements, notes, settings, and reports are stored locally on this device.")
        }
        SectionCard {
            Text("License and libraries", style = MaterialTheme.typography.titleMedium)
            Text("Built with Kotlin, Jetpack Compose, Material 3, Room, Navigation Compose, Coroutines, and ARCore availability checks.")
        }
    }
}
