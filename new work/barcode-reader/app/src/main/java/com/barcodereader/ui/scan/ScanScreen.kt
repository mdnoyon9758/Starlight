package com.barcodereader.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.barcodereader.R
import com.barcodereader.ui.components.ScanResultDisplay
import com.barcodereader.util.FormatUtils
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
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
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scanResult by viewModel.scanResult.collectAsState()
    val productInfo by viewModel.productInfo.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showResult by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            scanImageFromUri(context, photoUri!!) { result, type ->
                viewModel.processScan(result, type)
                showResult = true
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scanImageFromUri(context, uri) { result, type ->
                viewModel.processScan(result, type)
                showResult = true
            }
        }
    }

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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.scan_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Text(
                            stringResource(R.string.scan_processing),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                } else if (!showResult) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.scan_capture),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                stringResource(R.string.scan_instructions),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(stringResource(R.string.scan_take_photo), style = MaterialTheme.typography.titleMedium)
                            }

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
                                Text(stringResource(R.string.scan_load_gallery), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }

        // Result bottom sheet
        if (showResult) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val currentResult = scanResult
                    if (currentResult != null) {
                        ScanResultDisplay(
                            result = currentResult.content,
                            type = currentResult.type,
                            productInfo = productInfo,
                            onCopy = {
                                FormatUtils.copyToClipboard(context, currentResult.content)
                            },
                            onShare = {
                                FormatUtils.shareText(context, currentResult.content)
                            },
                            onDismiss = {
                                showResult = false
                                viewModel.clearResult()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showResult = false
                            viewModel.clearResult()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.scan_done), style = MaterialTheme.typography.titleMedium)
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
                    onResult(context.getString(R.string.scan_no_barcode), "TEXT")
                }
            }
            .addOnFailureListener {
                onResult(context.getString(R.string.scan_failed), "TEXT")
            }
    } catch (e: Exception) {
        onResult(context.getString(R.string.scan_error), "TEXT")
    }
}