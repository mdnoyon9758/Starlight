package com.measuremate.features.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.measuremate.core.ui.FeatureCard
import com.measuremate.navigation.Route

private data class DashboardItem(val title: String, val subtitle: String, val icon: ImageVector, val route: Route)

@Composable
fun DashboardScreen(navController: NavController) {
    val items = listOf(
        DashboardItem("AR Measure", "Camera based distance and area tools", Icons.Rounded.Straighten, Route.Ar),
        DashboardItem("Manual Measure", "Length, width, height, volume", Icons.Rounded.Straighten, Route.Manual),
        DashboardItem("Calculator", "Shapes and material estimates", Icons.Rounded.Calculate, Route.Calculator),
        DashboardItem("Projects", "Folders, measurements, notes", Icons.Rounded.Folder, Route.Projects),
        DashboardItem("Level Tool", "Bubble, angle, slope, compass", Icons.Rounded.Sensors, Route.Level),
        DashboardItem("Capability", "Hardware support check", Icons.Rounded.Sensors, Route.Capability),
        DashboardItem("Reports", "Offline PDF export", Icons.Rounded.Assessment, Route.Reports),
        DashboardItem("Settings", "Theme, units, precision", Icons.Rounded.Tune, Route.Settings)
    )
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("MeasureMate", style = MaterialTheme.typography.headlineMedium)
        Text("Offline measurement toolkit for site work, interiors, surveying, and home projects.", style = MaterialTheme.typography.bodyMedium)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                FeatureCard(item.title, item.subtitle, item.icon) { navController.navigate(item.route.path) }
            }
        }
    }
}
