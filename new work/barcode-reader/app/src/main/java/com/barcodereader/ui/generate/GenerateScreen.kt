package com.barcodereader.ui.generate

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barcodereader.R
import com.barcodereader.ui.components.EmptyState
import com.barcodereader.util.FormatUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

@Composable
fun GenerateScreen() {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(GenerateType.QR_CODE) }
    var inputText by remember { mutableStateOf("") }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showPreview by remember { mutableStateOf(false) }

    val types = listOf(
        GenerateType.QR_CODE,
        GenerateType.CODE_128,
        GenerateType.EAN_13,
        GenerateType.DATA_MATRIX,
        GenerateType.PDF_417
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Generate Codes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Type selector
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Code Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(types) { type ->
                            TypeChip(
                                type = type,
                                isSelected = selectedType == type,
                                onClick = {
                                    selectedType = type
                                    generatedBitmap = null
                                    showPreview = false
                                }
                            )
                        }
                    }
                }
            }

            // Input field
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Content",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            generatedBitmap = null
                            showPreview = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter text, URL, or data...") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        visualTransformation = VisualTransformation.None,
                        singleLine = false,
                        maxLines = 4
                    )
                }
            }

            // Generate button
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        generatedBitmap = generateBarcode(inputText, selectedType)
                        showPreview = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = inputText.isNotBlank()
            ) {
                Text("Generate", style = MaterialTheme.typography.labelLarge)
            }

            // Preview
            if (showPreview && generatedBitmap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(300.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = generatedBitmap!!.asImageBitmap(),
                            contentDescription = "Generated barcode",
                            modifier = Modifier.size(250.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }

                // Actions
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { FormatUtils.copyToClipboard(context, inputText) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy Text")
                            }
                            Button(
                                onClick = { FormatUtils.shareImage(context, generatedBitmap!!) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = "Share")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Image")
                            }
                            Button(
                                onClick = { FormatUtils.saveImageToGallery(context, generatedBitmap!!) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Download, contentDescription = "Save")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Image")
                            }
                        }
                    }
                }
            } else if (inputText.isBlank()) {
                EmptyState(
                    icon = R.drawable.ic_qr_code,
                    title = "Create Your Own Codes",
                    subtitle = "Generate QR codes, barcodes, and more. Enter content above and tap Generate."
                )
            }
        }
    }
}

@Composable
fun TypeChip(
    type: GenerateType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Button(
        onClick = onClick,
        colors = colors,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(id = type.iconRes), contentDescription = null, modifier = Modifier.size(20.dp))
            Text(type.label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

enum class GenerateType(val label: String, val iconRes: Int) {
    QR_CODE("QR Code", R.drawable.ic_qr_code),
    CODE_128("Code 128", R.drawable.ic_barcode),
    EAN_13("EAN-13", R.drawable.ic_ean),
    DATA_MATRIX("Data Matrix", R.drawable.ic_datamatrix),
    PDF_417("PDF417", R.drawable.ic_pdf417)
}

fun generateBarcode(content: String, type: GenerateType): Bitmap {
    val writer = when (type) {
        GenerateType.QR_CODE -> QRCodeWriter()
        else -> MultiFormatWriter()
    }

    val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
    hints[EncodeHintType.MARGIN] = 2
    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M

    val format = when (type) {
        GenerateType.QR_CODE -> BarcodeFormat.QR_CODE
        GenerateType.CODE_128 -> BarcodeFormat.CODE_128
        GenerateType.EAN_13 -> BarcodeFormat.EAN_13
        GenerateType.DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
        GenerateType.PDF_417 -> BarcodeFormat.PDF_417
    }

    val bitMatrix: BitMatrix = writer.encode(content, format, 500, 500, hints)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
        }
    }
    return bitmap
}
