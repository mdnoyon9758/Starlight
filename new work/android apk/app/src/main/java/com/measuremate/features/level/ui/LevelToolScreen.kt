package com.measuremate.features.level.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.measuremate.core.sensors.LevelSensor
import com.measuremate.core.ui.SectionCard
import kotlin.math.abs

@Composable
fun LevelToolScreen() {
    val context = LocalContext.current
    val sensor = remember { LevelSensor(context) }
    val reading by sensor.readings().collectAsState(initial = com.measuremate.core.sensors.LevelReading())
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Level Tool", style = MaterialTheme.typography.headlineSmall)
        if (!reading.supported) {
            SectionCard {
                Text("Accelerometer unavailable", style = MaterialTheme.typography.titleMedium)
                Text("Spirit level, angle, and slope tools require an accelerometer.")
            }
        } else {
            SectionCard {
                Text("Digital Level", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("X angle: ${reading.xAngle} deg")
                    Text("Y angle: ${reading.yAngle} deg")
                }
                LinearProgressIndicator(progress = { (1f - abs(reading.slope).coerceAtMost(45) / 45f) }, modifier = Modifier.fillMaxWidth())
                Text("Slope: ${reading.slope} deg")
                Text("Compass: ${reading.compass?.let { "$it deg" } ?: "Unavailable"}")
            }
            SectionCard {
                Text("Modes included", style = MaterialTheme.typography.titleMedium)
                Text("Spirit level, bubble level, digital level, angle meter, slope meter, and compass share the same sensor feed in this first APK.")
            }
        }
    }
}
