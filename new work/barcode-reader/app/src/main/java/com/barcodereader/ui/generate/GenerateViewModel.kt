package com.barcodereader.ui.generate

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.barcodereader.domain.usecase.GeneratorUseCases
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Generate ViewModel
 *
 * Manages state for the generator screen.
 */
@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val generatorUseCases: GeneratorUseCases
) : ViewModel() {

    private val _selectedType = MutableStateFlow(GenerateType.QR_CODE)
    val selectedType: StateFlow<GenerateType> = _selectedType.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _generatedBitmap = MutableStateFlow<Bitmap?>(null)
    val generatedBitmap: StateFlow<Bitmap?> = _generatedBitmap.asStateFlow()

    private val _foreground = MutableStateFlow(0xFF000000.toInt())
    val foreground: StateFlow<Int> = _foreground.asStateFlow()

    private val _background = MutableStateFlow(0xFFFFFFFF.toInt())
    val background: StateFlow<Int> = _background.asStateFlow()

    private val _errorCorrection = MutableStateFlow(ErrorCorrectionLevel.M)
    val errorCorrection: StateFlow<ErrorCorrectionLevel> = _errorCorrection.asStateFlow()

    /**
     * Set selected type
     */
    fun setSelectedType(type: GenerateType) {
        _selectedType.value = type
        _generatedBitmap.value = null
    }

    /**
     * Set input text
     */
    fun setInputText(text: String) {
        _inputText.value = text
        _generatedBitmap.value = null
    }

    /**
     * Set foreground color
     */
    fun setForegroundColor(color: Int) {
        _foreground.value = color
    }

    /**
     * Set background color
     */
    fun setBackgroundColor(color: Int) {
        _background.value = color
    }

    /**
     * Set error correction level
     */
    fun setErrorCorrection(level: ErrorCorrectionLevel) {
        _errorCorrection.value = level
    }

    /**
     * Generate code
     */
    fun generate() {
        val content = _inputText.value
        if (content.isBlank()) return

        val bitmap = when (_selectedType.value) {
            GenerateType.QR_CODE -> generatorUseCases.generateQrCode(
                content,
                foregroundColor = _foreground.value,
                backgroundColor = _background.value,
                errorCorrection = _errorCorrection.value
            )
            else -> generatorUseCases.generateBarcode(
                content,
                format = _selectedType.value.format,
                foregroundColor = _foreground.value,
                backgroundColor = _background.value
            )
        }

        _generatedBitmap.value = bitmap
    }

    /**
     * Clear generated bitmap
     */
    fun clearGenerated() {
        _generatedBitmap.value = null
    }
}

/**
 * Generate type options
 */
enum class GenerateType(val label: String, val format: BarcodeFormat) {
    QR_CODE("QR Code", BarcodeFormat.QR_CODE),
    CODE_128("Code 128", BarcodeFormat.CODE_128),
    EAN_13("EAN-13", BarcodeFormat.EAN_13),
    DATA_MATRIX("Data Matrix", BarcodeFormat.DATA_MATRIX),
    PDF_417("PDF417", BarcodeFormat.PDF_417),
    CODE_39("Code 39", BarcodeFormat.CODE_39),
    ITF("ITF", BarcodeFormat.ITF),
    UPC_A("UPC-A", BarcodeFormat.UPC_A)
}