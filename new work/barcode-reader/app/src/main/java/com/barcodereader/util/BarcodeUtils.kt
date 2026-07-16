package com.barcodereader.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.mlkit.vision.barcode.common.Barcode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BarcodeUtils {
    fun getBarcodeTypeName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_AZTEC -> "Aztec"
            Barcode.FORMAT_CODABAR -> "Codabar"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_CODE_93 -> "Code 93"
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            Barcode.FORMAT_EAN_8 -> "EAN-8"
            Barcode.FORMAT_EAN_13 -> "EAN-13"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_UPC_A -> "UPC-A"
            Barcode.FORMAT_UPC_E -> "UPC-E"
            else -> "Unknown"
        }
    }

    fun getBarcodeTypeColor(format: Int): Long {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> 0xFF7C3AED
            Barcode.FORMAT_EAN_8, Barcode.FORMAT_EAN_13 -> 0xFF0891B2
            Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODE_93 -> 0xFF059669
            Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E -> 0xFFD97706
            Barcode.FORMAT_PDF417 -> 0xFFDC2626
            Barcode.FORMAT_DATA_MATRIX, Barcode.FORMAT_AZTEC -> 0xFF2563EB
            else -> 0xFF4F46E5
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("barcode", text)
        clipboard.setPrimaryClip(clip)
    }

    fun shareText(context: Context, text: String, type: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            this.type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Scanned $type")
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun generateQrCode(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val bits = com.google.zxing.qrcode.QRCodeWriter().encode(
                content,
                com.google.zxing.BarcodeFormat.QR_CODE,
                size,
                size
            )
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x, y,
                        if (bits[x, y]) foregroundColor else backgroundColor
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun generateBarcode(
        content: String,
        format: com.google.zxing.BarcodeFormat = com.google.zxing.BarcodeFormat.CODE_128,
        width: Int = 1024,
        height: Int = 256,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val bits = com.google.zxing.oned.Code128Writer().encode(
                content,
                format,
                width,
                height
            )
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x, y,
                        if (bits[x, y]) foregroundColor else backgroundColor
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}