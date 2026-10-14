package com.bolusbuddy.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bolusbuddy.app.navigation.BottomTab

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(tabNavController) }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Home.route) {
                HomeScreen(onNavigate = { tab -> tabNavController.navigateToTab(tab) })
            }
            composable(BottomTab.Scan.route) { ScanScreen() }      // ← new line
            composable(BottomTab.GlucoseCarbs.route) { GlucoseCarbsScreen() }
            composable(BottomTab.Bolus.route) { BolusScreen() }
            composable(BottomTab.Settings.route) { SettingsScreen(onLogout = onLogout) }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        BottomTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { navController.navigateToTab(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) }
            )
        }
    }
}

fun NavHostController.navigateToTab(tab: BottomTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

