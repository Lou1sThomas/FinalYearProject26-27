package com.bolusbuddy.app.data

import com.google.gson.annotations.SerializedName

/** The whole reply from Open Food Facts. */

data class ProductResponse(
    val status: Int,
    val product: Product?
)

/** The product details I'm asking for */
data class Product(
    @SerializedName("product_name") val productName: String?,
    val brands: String?,
    @SerializedName("serving_size") val servingSize: String?,
    @SerializedName("serving_quantity") val servingQuantity: String?,
    val nutriments: Nutriments?
)

/** Nutrition values. Any of them can be missing for a product. */

data class Nutriments(
    @SerializedName("carbohydrates_100g") val carbsPer100g: Double?,
    @SerializedName("carbohydrates_serving") val carbsPerServing: Double?,
    @SerializedName("sugars_100g") val sugarsPer100g: Double?
)

