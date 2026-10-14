package com.bolusbuddy.app.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Describes the one request we make to Open Food Facts. */
interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}")
    suspend fun getProduct(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String =
            "product_name,brands,serving_size,serving_quantity,nutriments"
    ): ProductResponse
}

/** Builds the API once and shares it across the app. */
object OpenFoodFactsClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    // Open Food Facts asks apps to identify themselves
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "BolusBuddy/0.1 (Android; student project)")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: OpenFoodFactsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenFoodFactsApi::class.java)
}