package com.barcodereader.domain.usecase

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import javax.inject.Inject

/**
 * Generator Use Cases
 *
 * Grouped use cases for barcode/QR code generation.
 */
class GeneratorUseCases @Inject constructor() {

    /**
     * Generate QR code
     */
    fun generateQrCode(
        content: String,
        size: Int = 800,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
        margin: Int = 2
    ): Bitmap? {
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = margin
            hints[EncodeHintType.ERROR_CORRECTION] = errorCorrection

            val writer = QRCodeWriter()
            val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

            createBitmapFromMatrix(bitMatrix, foregroundColor, backgroundColor)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generate barcode
     */
    fun generateBarcode(
        content: String,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
        width: Int = 1024,
        height: Int = 256,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = 0

            val bitMatrix: BitMatrix = writer.encode(content, format, width, height, hints)

            createBitmapFromMatrix(bitMatrix, foregroundColor, backgroundColor)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generate WiFi QR code
     */
    fun generateWifiQr(
        ssid: String,
        password: String,
        security: String = "WPA",
        size: Int = 800
    ): Bitmap? {
        val wifiString = "WIFI:T:$security;S:$ssid;P:$password;;"
        return generateQrCode(wifiString, size)
    }

    /**
     * Generate email QR code
     */
    fun generateEmailQr(
        email: String,
        subject: String = "",
        body: String = "",
        size: Int = 800
    ): Bitmap? {
        val emailString = "mailto:$email?subject=$subject&body=$body"
        return generateQrCode(emailString, size)
    }

    /**
     * Generate phone QR code
     */
    fun generatePhoneQr(phone: String, size: Int = 800): Bitmap? {
        return generateQrCode("tel:$phone", size)
    }

    /**
     * Generate SMS QR code
     */
    fun generateSmsQr(phone: String, message: String = "", size: Int = 800): Bitmap? {
        val smsString = "sms:$phone?body=$message"
        return generateQrCode(smsString, size)
    }

    /**
     * Generate location QR code
     */
    fun generateLocationQr(latitude: Double, longitude: Double, size: Int = 800): Bitmap? {
        return generateQrCode("geo:$latitude,$longitude", size)
    }

    /**
     * Generate vCard QR code
     */
    fun generateVCardQr(
        name: String,
        phone: String = "",
        email: String = "",
        organization: String = "",
        size: Int = 800
    ): Bitmap? {
        val vcard = buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            appendLine("FN:$name")
            if (phone.isNotBlank()) appendLine("TEL:$phone")
            if (email.isNotBlank()) appendLine("EMAIL:$email")
            if (organization.isNotBlank()) appendLine("ORG:$organization")
            appendLine("END:VCARD")
        }
        return generateQrCode(vcard, size)
    }

    /**
     * Generate WhatsApp QR code
     */
    fun generateWhatsAppQr(phone: String, message: String = "", size: Int = 800): Bitmap? {
        val whatsappString = "https://wa.me/$phone?text=$message"
        return generateQrCode(whatsappString, size)
    }

    /**
     * Generate Bitcoin QR code
     */
    fun generateBitcoinQr(address: String, amount: Double? = null, size: Int = 800): Bitmap? {
        val bitcoinString = if (amount != null) {
            "bitcoin:$address?amount=$amount"
        } else {
            "bitcoin:$address"
        }
        return generateQrCode(bitcoinString, size)
    }

    /**
     * Generate Ethereum QR code
     */
    fun generateEthereumQr(address: String, size: Int = 800): Bitmap? {
        return generateQrCode(address, size)
    }

    private fun createBitmapFromMatrix(
        bitMatrix: BitMatrix,
        foregroundColor: Int,
        backgroundColor: Int
    ): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) foregroundColor else backgroundColor)
            }
        }

        return bitmap
    }
}