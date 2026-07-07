package com.measuremate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.measuremate.core.theme.MeasureMateTheme
import com.measuremate.data.database.MeasureMateDatabase
import com.measuremate.data.repository.MeasureMateRepository
import com.measuremate.features.about.ui.AboutScreen
import com.measuremate.features.ar.ui.ArMeasureScreen
import com.measuremate.features.calculator.ui.CalculatorScreen
import com.measuremate.features.capability.ui.CapabilityScreen
import com.measuremate.features.dashboard.ui.DashboardScreen
import com.measuremate.features.level.ui.LevelToolScreen
import com.measuremate.features.manual.ui.ManualMeasureScreen
import com.measuremate.features.projects.ui.ProjectsScreen
import com.measuremate.features.reports.ui.ReportsScreen
import com.measuremate.features.settings.ui.SettingsScreen
import com.measuremate.navigation.Route

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MeasureMateRepository(MeasureMateDatabase.get(this).dao())
        setContent {
            MeasureMateTheme {
                MeasureMateApp(repository)
            }
        }
    }
}

@Composable
private fun MeasureMateApp(repository: MeasureMateRepository) {
    val navController = rememberNavController()
    val appViewModel: MeasureMateViewModel = viewModel(factory = MeasureMateViewModel.factory(repository))
    val bottomItems = listOf(
        Route.Dashboard to Icons.Rounded.Home,
        Route.Projects to Icons.Rounded.Folder,
        Route.Calculator to Icons.Rounded.Calculate,
        Route.Settings to Icons.Rounded.Settings
    )
    Scaffold(
        bottomBar = {
            val entry = navController.currentBackStackEntryAsState().value
            NavigationBar {
                bottomItems.forEach { (route, icon) ->
                    val selected = entry?.destination?.hierarchy?.any { it.route == route.path } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(route.path) {
                                popUpTo(Route.Dashboard.path)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = route.path) },
                        label = { Text(route.path.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = Route.Dashboard.path, modifier = Modifier.padding(padding)) {
            composable(Route.Dashboard.path) { DashboardScreen(navController) }
            composable(Route.Capability.path) { CapabilityScreen() }
            composable(Route.Ar.path) { ArMeasureScreen(appViewModel) }
            composable(Route.Manual.path) { ManualMeasureScreen(appViewModel) }
            composable(Route.Calculator.path) { CalculatorScreen() }
            composable(Route.Level.path) { LevelToolScreen() }
            composable(Route.Projects.path) { ProjectsScreen(appViewModel) }
            composable(Route.Reports.path) { ReportsScreen(appViewModel) }
            composable(Route.Settings.path) { SettingsScreen(appViewModel, onAbout = { navController.navigate(Route.About.path) }) }
            composable(Route.About.path) { AboutScreen() }
        }
    }
}
