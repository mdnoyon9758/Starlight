package com.barcodereader.data

data class ScanHistory(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val format: String = "",
    val type: String = "UNKNOWN",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val formattedDate: String
        get() {
            val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }

    val displayType: String
        get() = when (type.uppercase()) {
            "QR_CODE" -> "QR Code"
            "CODE_128" -> "Code 128"
            "CODE_39" -> "Code 39"
            "EAN_13" -> "EAN-13"
            "EAN_8" -> "EAN-8"
            "UPC_A" -> "UPC-A"
            "UPC_E" -> "UPC-E"
            "PDF417" -> "PDF417"
            "DATA_MATRIX" -> "Data Matrix"
            "AZTEC" -> "Aztec"
            "CODABAR" -> "Codabar"
            "CODE_93" -> "Code 93"
            "ITF" -> "ITF"
            "MAXICODE" -> "MaxiCode"
            "RSS_14" -> "RSS-14"
            "RSS_EXPANDED" -> "RSS Expanded"
            else -> type.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
        }
}