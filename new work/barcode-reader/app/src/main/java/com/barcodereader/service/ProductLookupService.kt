package com.barcodereader.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class ProductInfo(
    val barcode: String,
    val name: String?,
    val brand: String?,
    val imageUrl: String?,
    val categories: String?,
    val ingredients: String?,
    val nutriscore: String?
)

class ProductLookupService(private val context: Context) {
    
    private val cache = LruCache<String, ProductInfo>(100)
    private val imageCache = LruCache<String, Bitmap>(50)
    
    companion object {
        private const val API_BASE_URL = "https://world.openfoodfacts.org/api/v2/product/"
        private const val IMAGE_BASE_URL = "https://images.openfoodfacts.org/images/products/"
        private const val TIMEOUT_MS = 5000
    }
    
    suspend fun lookupProduct(barcode: String): ProductInfo? {
        // Check cache first
        cache.get(barcode)?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$API_BASE_URL$barcode.json")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("User-Agent", "BarcodeReader/2.0")
                }
                
                val responseCode = connection.responseCode
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()
                    
                    val json = JSONObject(response)
                    
                    if (json.getInt("status") == 1) {
                        val product = json.getJSONObject("product")
                        
                        val productInfo = ProductInfo(
                            barcode = barcode,
                            name = product.optString("product_name", null),
                            brand = product.optString("brands", null),
                            imageUrl = product.optString("image_front_url", null),
                            categories = product.optString("categories", null),
                            ingredients = product.optString("ingredients_text", null),
                            nutriscore = product.optString("nutriscore_grade", null)
                        )
                        
                        cache.put(barcode, productInfo)
                        productInfo
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    suspend fun getProductImage(imageUrl: String): Bitmap? {
        // Check cache first
        imageCache.get(imageUrl)?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.apply {
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                }
                
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                
                if (bitmap != null) {
                    imageCache.put(imageUrl, bitmap)
                }
                
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    fun isProductBarcode(barcode: String): Boolean {
        // EAN-13, UPC-A, UPC-E, EAN-8
        return barcode.matches(Regex("^\\d{8,13}$"))
    }
}
