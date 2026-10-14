package com.bolusbuddy.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-Level routes: before login vs inside the app */

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
}

/** The tabs shown in the bottom nav bar */
enum class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home("home", "Home", Icons.Filled.Home),

    Scan("scan", "Scan", Icons.Filled.QrCodeScanner),
    GlucoseCarbs("glucose_carbs", "Glucose", Icons.Filled.WaterDrop),
    Bolus("bolus", "Bolus", Icons.Filled.Calculate),
    Settings("settings", "Settings", Icons.Filled.Settings)

}


