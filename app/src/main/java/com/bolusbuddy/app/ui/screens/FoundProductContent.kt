package com.bolusbuddy.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bolusbuddy.app.data.ProductLookupResult
import com.bolusbuddy.app.domain.CarbCalculator
import java.util.Locale

private enum class PortionMode { Grams, Servings }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundProductContent(product: ProductLookupResult.Found) {
    val canUseGrams = product.carbsPer100g != null
    val canUseServings = product.carbsPerServing != null ||
            (product.carbsPer100g != null && product.servingGrams != null)

    var mode by remember {
        mutableStateOf(if (canUseGrams) PortionMode.Grams else PortionMode.Servings)
    }
    var amountText by remember { mutableStateOf("") }

    // Accept "1,5" as well as "1.5"
    val amount = amountText.replace(',', '.').toDoubleOrNull()
    val maxAmount = if (mode == PortionMode.Grams) 5000.0 else 50.0

    val totalCarbs: Double? =
        if (amount == null || amount <= 0 || amount > maxAmount) {
            null
        } else {
            when (mode) {
                PortionMode.Grams ->
                    product.carbsPer100g?.let { CarbCalculator.carbsForGrams(it, amount) }
                PortionMode.Servings ->
                    CarbCalculator.carbsForServings(
                        servings = amount,
                        carbsPerServing = product.carbsPerServing,
                        carbsPer100g = product.carbsPer100g,
                        servingGrams = product.servingGrams
                    )
            }
        }

    // Product details
    Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    product.brand?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    Spacer(Modifier.height(12.dp))
    HorizontalDivider()
    Spacer(Modifier.height(12.dp))

    NutrientRow("Carbs per 100 g", formatGrams(product.carbsPer100g))
    if (product.carbsPerServing != null) {
        val label = product.servingSize?.let { "Carbs per serving ($it)" } ?: "Carbs per serving"
        NutrientRow(label, formatGrams(product.carbsPerServing))
    }

    if (!canUseGrams && !canUseServings) {
        Text(
            "No carb information for this product. Check the packet.",
            color = MaterialTheme.colorScheme.error
        )
    } else {
        // Portion input
        Spacer(Modifier.height(16.dp))
        Text("How much are you eating?", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (canUseGrams) {
                FilterChip(
                    selected = mode == PortionMode.Grams,
                    onClick = { mode = PortionMode.Grams; amountText = "" },
                    label = { Text("Grams") }
                )
            }
            if (canUseServings) {
                FilterChip(
                    selected = mode == PortionMode.Servings,
                    onClick = { mode = PortionMode.Servings; amountText = "" },
                    label = { Text("Servings") }
                )
            }
        }

        OutlinedTextField(
            value = amountText,
            onValueChange = { new ->
                // Only digits and a decimal point, max 7 characters
                if (new.length <= 7 && new.all { it.isDigit() || it == '.' || it == ',' }) {
                    amountText = new
                }
            },
            label = {
                Text(if (mode == PortionMode.Grams) "Amount (g)" else "Number of servings")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        if (amountText.isNotEmpty() && totalCarbs == null) {
            Text(
                "Enter an amount between 0 and ${maxAmount.toInt()}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        totalCarbs?.let { carbs ->
            Spacer(Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total carbs", style = MaterialTheme.typography.titleMedium)
                    Text(
                        formatGrams(carbs),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        "Data from Open Food Facts. Always check against the packet.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatGrams(value: Double?): String =
    value?.let { String.format(Locale.UK, "%.1f g", it) } ?: "Not listed"