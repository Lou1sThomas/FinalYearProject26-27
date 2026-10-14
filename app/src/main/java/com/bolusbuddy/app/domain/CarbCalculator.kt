package com.bolusbuddy.app.domain

/**
 * Pure carb maths with no UI or Android code, so it can be unit tested.
 */
object CarbCalculator {

    /** Carbs in a weighed portion, e.g. 50 g of something with 60 g carbs per 100 g = 30 g. */
    fun carbsForGrams(carbsPer100g: Double, grams: Double): Double {
        require(carbsPer100g >= 0 && grams >= 0) { "Values can't be negative" }
        return carbsPer100g * grams / 100.0
    }

    /**
     * Carbs for a number of servings.
     * Uses the label's per-serving figure if there is one,
     * otherwise works it out from the serving weight.
     * Returns null if there isn't enough information.
     */
    fun carbsForServings(
        servings: Double,
        carbsPerServing: Double?,
        carbsPer100g: Double?,
        servingGrams: Double?
    ): Double? {
        require(servings >= 0) { "Servings can't be negative" }
        return when {
            carbsPerServing != null -> carbsPerServing * servings
            carbsPer100g != null && servingGrams != null ->
                carbsForGrams(carbsPer100g, servingGrams * servings)
            else -> null
        }
    }
}