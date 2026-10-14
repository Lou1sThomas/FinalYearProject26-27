package com.bolusbuddy.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GlucoseCarbsScreen() {
    PlaceholderContent("Glucose & Carbs", "Enter current BG and meal carbs here.")
}

@Composable
fun BolusScreen() {
    PlaceholderContent("Bolus Calculator", "The bolus formula engine will live here.")
}

@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    PlaceholderContent("Settings", "I:C ratio, ISF and target BG will be saved here.") {
        Spacer(Modifier.height(24.dp))
        Button(onClick = onLogout) { Text("Log out") }
    }
}

@Composable
private fun PlaceholderContent(
    title: String,
    description: String,
    extra: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        extra()
    }
}