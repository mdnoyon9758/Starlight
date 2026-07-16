package com.barcodereader.data.repository

import com.barcodereader.config.ApiConfig
import com.barcodereader.di.IoDispatcher
import com.barcodereader.service.ProductInfo
import com.barcodereader.service.ProductLookupService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Product Repository
 *
 * Handles product lookup operations.
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productLookupService: ProductLookupService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Look up product information by barcode
     */
    suspend fun lookupProduct(barcode: String): ProductInfo? {
        if (!ApiConfig.PRODUCT_LOOKUP_ENABLED) return null
        return withContext(ioDispatcher) {
            productLookupService.lookupProduct(barcode)
        }
    }

    /**
     * Check if barcode is a product barcode
     */
    fun isProductBarcode(barcode: String): Boolean {
        return productLookupService.isProductBarcode(barcode)
    }
}