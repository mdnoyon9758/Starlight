package com.barcodereader.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.barcodereader.R
import com.barcodereader.data.HistoryStorage
import com.barcodereader.data.ScanHistory
import com.barcodereader.service.ProductInfo
import com.barcodereader.service.ProductLookupService
import com.barcodereader.ui.components.EmptyState
import com.barcodereader.ui.components.ScanResultDisplay
import com.barcodereader.ui.theme.Primary
import com.barcodereader.util.FormatUtils
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private fun getFormatName(content: String): String {
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

@Composable
fun ScanScreen(
    onGalleryClick: () -> Unit,
    onPermissionRequest: () -> Unit
) {
    val context = LocalContext.current
    val storage = remember { HistoryStorage(context) }
    val productLookupService = remember { ProductLookupService(context) }
    val scope = rememberCoroutineScope()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isProcessing by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<String?>(null) }
    var lastResultType by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var productInfo by remember { mutableStateOf<ProductInfo?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera capture launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            isProcessing = true
            scanImageFromUri(context, photoUri!!) { result, type ->
                lastResult = result
                lastResultType = type
                showResult = true
                isProcessing = false
                saveToHistory(storage, result, type)
                lookupProduct(productLookupService, result) { info ->
                    productInfo = info
                }
            }
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isProcessing = true
            scanImageFromUri(context, uri) { result, type ->
                lastResult = result
                lastResultType = type
                showResult = true
                isProcessing = false
                saveToHistory(storage, result, type)
                lookupProduct(productLookupService, result) { info ->
                    productInfo = info
                }
            }
        }
    }

    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            val uri = createImageUri(context)
            photoUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Scan Barcode",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Main content area
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    // Loading state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = Primary,
                            strokeWidth = 4.dp
                        )
                        Text(
                            "Scanning barcode...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                } else if (!showResult) {
                    // Ready to scan state
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Camera icon
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Capture to Scan",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Take a photo or choose from gallery\nto scan barcodes and QR codes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Action buttons
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Take Photo button
                            Button(
                                onClick = {
                                    if (hasPermission) {
                                        val uri = createImageUri(context)
                                        photoUri = uri
                                        cameraLauncher.launch(uri)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Take Photo", style = MaterialTheme.typography.titleMedium)
                            }

                            // Gallery button
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Load from Gallery", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }

        // Result bottom sheet
        AnimatedVisibility(
            visible = showResult && lastResult != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        showResult = false
                        lastResult = null
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 8.dp)
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Result display
                    ScanResultDisplay(
                        result = lastResult!!,
                        type = lastResultType ?: "TEXT",
                        productInfo = productInfo,
                        onCopy = {
                            FormatUtils.copyToClipboard(context, lastResult!!)
                        },
                        onShare = {
                            FormatUtils.shareText(context, lastResult!!)
                        },
                        onDismiss = {
                            showResult = false
                            lastResult = null
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Done button
                    OutlinedButton(
                        onClick = {
                            showResult = false
                            lastResult = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Done", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val imageDir = File(context.cacheDir, "images")
    imageDir.mkdirs()
    val imageFile = File(imageDir, "barcode_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

private fun scanImageFromUri(
    context: Context,
    uri: Uri,
    onResult: (String, String) -> Unit
) {
    try {
        val image = InputImage.fromFilePath(context, uri)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull()
                if (barcode != null) {
                    val value = barcode.rawValue ?: ""
                    val type = getFormatName(value)
                    onResult(value, type)
                } else {
                    onResult("No barcode found in the image", "TEXT")
                }
            }
            .addOnFailureListener {
                onResult("Failed to scan barcode", "TEXT")
            }
    } catch (e: Exception) {
        onResult("Error loading image", "TEXT")
    }
}

private fun saveToHistory(storage: HistoryStorage, content: String, type: String) {
    val entry = ScanHistory(
        id = System.currentTimeMillis(),
        content = content,
        type = type,
        format = getFormatName(content),
        timestamp = System.currentTimeMillis()
    )
    storage.addEntry(entry)
}

private fun lookupProduct(
    service: ProductLookupService,
    barcode: String,
    onResult: (ProductInfo?) -> Unit
) {
    if (service.isProductBarcode(barcode)) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val info = service.lookupProduct(barcode)
                onResult(info)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}
