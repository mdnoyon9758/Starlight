package com.measuremate.features.calculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.measuremate.core.ui.SectionCard
import com.measuremate.core.utils.ShapeMath

@Composable
fun CalculatorScreen() {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    val x = a.toDoubleOrNull() ?: 0.0
    val y = b.toDoubleOrNull() ?: 0.0
    val z = c.toDoubleOrNull() ?: 0.0

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("Calculator Center", style = MaterialTheme.typography.headlineSmall)
            Text("Use A, B, and C as length/radius/base/height/depth depending on the formula.")
        }
        item {
            SectionCard {
                Input("A", a) { a = it }
                Input("B", b) { b = it }
                Input("C", c) { c = it }
            }
        }
        item {
            SectionCard {
                Text("Shapes", style = MaterialTheme.typography.titleMedium)
                Result("Rectangle area", ShapeMath.rectangle(x, y))
                Result("Circle area", ShapeMath.circle(x))
                Result("Triangle area", ShapeMath.triangle(x, y))
                Result("Cylinder volume", ShapeMath.cylinderVolume(x, y))
                Result("Cube volume", ShapeMath.cubeVolume(x))
                Result("Cone volume", ShapeMath.coneVolume(x, y))
                Result("Sphere volume", ShapeMath.sphereVolume(x))
            }
        }
        item {
            SectionCard {
                Text("Materials", style = MaterialTheme.typography.titleMedium)
                Result("Paint liters", ShapeMath.paintLiters(x, y.coerceAtLeast(1.0), z.coerceAtLeast(1.0)))
                Result("Tile count", ShapeMath.tileCount(x, y.coerceAtLeast(0.01), z.coerceAtLeast(0.01), 10.0))
                Result("Flooring area", x * y)
                Result("Concrete volume", ShapeMath.concreteVolume(x, y, z))
                Result("Brick count", ShapeMath.brickCount(x, y.coerceAtLeast(1.0), 5.0))
                Result("Steel basic weight", x * y * 0.006165)
            }
        }
    }
}

@Composable
private fun Input(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Result(label: String, value: Double) {
    Text("$label: ${"%.2f".format(value)}")
}
