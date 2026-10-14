package com.bolusbuddy.app.data

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException

/** Every possible outcome of looking up a barcode. */
sealed interface ProductLookupResult {
    data class Found(
        val barcode: String,
        val name: String,
        val brand: String?,
        val carbsPer100g: Double?,
        val carbsPerServing: Double?,
        val servingSize: String?,
        val servingGrams: Double?
    ) : ProductLookupResult

    data object NotFound : ProductLookupResult

    data class Error(val message: String) : ProductLookupResult
}

/** The one place the app goes to look up a product. */
object ProductRepository {

    suspend fun lookUp(barcode: String): ProductLookupResult {
        return try {
            val response = OpenFoodFactsClient.api.getProduct(barcode)
            val product = response.product

            if (response.status != 1 || product == null) {
                ProductLookupResult.NotFound
            } else {
                ProductLookupResult.Found(
                    barcode = barcode,
                    name = product.productName?.takeIf { it.isNotBlank() } ?: "Unknown product",
                    brand = product.brands?.split(",")?.firstOrNull()?.trim()?.takeIf { it.isNotBlank() },
                    carbsPer100g = product.nutriments?.carbsPer100g,
                    carbsPerServing = product.nutriments?.carbsPerServing,
                    servingSize = product.servingSize?.takeIf { it.isNotBlank() },
                    servingGrams = product.servingQuantity?.toDoubleOrNull()
                )
            }
        } catch (e: HttpException) {
            // Open Food Facts replies 404 for barcodes it doesn't know
            if (e.code() == 404) ProductLookupResult.NotFound
            else ProductLookupResult.Error("Food database error (${e.code()}). Try again.")
        } catch (e: IOException) {
            ProductLookupResult.Error("No internet connection. Check your connection and try again.")
        } catch (e: JsonParseException) {
            ProductLookupResult.Error("Couldn't read the product data for this item.")
        }
    }
}