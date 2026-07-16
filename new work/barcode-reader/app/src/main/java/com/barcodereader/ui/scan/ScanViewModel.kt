package com.barcodereader.ui.scan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.barcodereader.domain.usecase.ScanUseCases
import com.barcodereader.domain.usecase.GeneratorUseCases
import com.barcodereader.service.ProductInfo
import com.barcodereader.service.ProductLookupService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Scan ViewModel
 *
 * Manages state for the scan screen.
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanUseCases: ScanUseCases,
    private val generatorUseCases: GeneratorUseCases,
    private val productLookupService: ProductLookupService
) : ViewModel() {

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _productInfo = MutableStateFlow<ProductInfo?>(null)
    val productInfo: StateFlow<ProductInfo?> = _productInfo.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    /**
     * Process scanned barcode
     */
    fun processScan(content: String, type: String) {
        viewModelScope.launch {
            _isProcessing.value = true

            try {
                // Save to history
                val scan = ScanHistoryEntity(
                    id = System.currentTimeMillis(),
                    content = content,
                    format = type,
                    type = detectContentType(content),
                    timestamp = System.currentTimeMillis()
                )
                scanUseCases.saveScan(scan)

                // Lookup product if applicable
                if (productLookupService.isProductBarcode(content)) {
                    val product = productLookupService.lookupProduct(content)
                    _productInfo.value = product
                }

                _scanResult.value = ScanResult(content, type)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    /**
     * Clear scan result
     */
    fun clearResult() {
        _scanResult.value = null
        _productInfo.value = null
    }

    /**
     * Detect content type
     */
    private fun detectContentType(content: String): String {
        return when {
            content.startsWith("http") -> "URL"
            content.contains("@") && content.contains(".") -> "EMAIL"
            content.matches(Regex("^\\+?[1-9]\\d{6,14}$")) -> "PHONE"
            content.matches(Regex("^\\d{8,13}$")) -> "PRODUCT"
            content.matches(Regex("^WIFI:")) -> "WIFI"
            content.matches(Regex("^BEGIN:VCARD", RegexOption.IGNORE_CASE)) -> "VCARD"
            content.matches(Regex("^geo:")) -> "LOCATION"
            else -> "TEXT"
        }
    }
}

/**
 * Scan result data class
 */
data class ScanResult(
    val content: String,
    val type: String
)