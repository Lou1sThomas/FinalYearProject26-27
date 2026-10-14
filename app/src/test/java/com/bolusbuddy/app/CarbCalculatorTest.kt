package com.bolusbuddy.app

import com.bolusbuddy.app.domain.CarbCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CarbCalculatorTest {

    @Test
    fun grams_50gOfFoodWith60gPer100g_is30g() {
        assertEquals(30.0, CarbCalculator.carbsForGrams(60.0, 50.0), 0.001)
    }

    @Test
    fun grams_zeroGrams_isZero() {
        assertEquals(0.0, CarbCalculator.carbsForGrams(60.0, 0.0), 0.001)
    }

    @Test
    fun servings_usesLabelPerServingFigureFirst() {
        // 1.5 servings × 30 g per serving = 45 g
        val result = CarbCalculator.carbsForServings(
            1.5, carbsPerServing = 30.0, carbsPer100g = 60.0, servingGrams = 40.0
        )
        assertEquals(45.0, result!!, 0.001)
    }

    @Test
    fun servings_fallsBackToServingWeight() {
        // 2 servings × 40 g = 80 g of food, at 60 g per 100 g = 48 g
        val result = CarbCalculator.carbsForServings(
            2.0, carbsPerServing = null, carbsPer100g = 60.0, servingGrams = 40.0
        )
        assertEquals(48.0, result!!, 0.001)
    }

    @Test
    fun servings_notEnoughInfo_returnsNull() {
        assertNull(CarbCalculator.carbsForServings(1.0, null, null, null))
    }

    @Test(expected = IllegalArgumentException::class)
    fun grams_negativeAmount_isRejected() {
        CarbCalculator.carbsForGrams(60.0, -10.0)
    }
}