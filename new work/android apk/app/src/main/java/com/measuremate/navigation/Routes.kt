package com.measuremate.navigation

sealed class Route(val path: String) {
    data object Dashboard : Route("dashboard")
    data object Capability : Route("capability")
    data object Ar : Route("ar")
    data object Manual : Route("manual")
    data object Calculator : Route("calculator")
    data object Level : Route("level")
    data object Projects : Route("projects")
    data object Reports : Route("reports")
    data object Settings : Route("settings")
    data object About : Route("about")
}
