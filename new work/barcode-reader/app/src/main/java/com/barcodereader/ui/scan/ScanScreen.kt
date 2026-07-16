package com.barcodereader.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.barcodereader.R
import com.barcodereader.data.HistoryStorage
import com.barcodereader.data.ScanHistory
import com.barcodereader.ui.components.LoadingOverlay
import com.barcodereader.ui.components.ScanOverlay
import com.barcodereader.util.FormatUtils
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private fun getFormatName(content: String): String {
    return when {
        content.startsWith("http") -> "URL"
        content.contains("@") && content.contains(".") -> "Email"
        content.matches(Regex("^\\+?[1-9]\\d{6,14}$")) -> "Phone"
        content.matches(Regex("^\\d{12,13}$")) -> "UPC/EAN"
        content.matches(Regex("^WIFI:")) -> "WiFi"
        content.matches(Regex("^BEGIN:VCARD", RegexOption.IGNORE_CASE)) -> "vCard"
        content.matches(Regex("^BEGIN:VCALENDAR", RegexOption.IGNORE_CASE)) -> "Calendar"
        content.matches(Regex("^geo:")) -> "Location"
        content.matches(Regex("^sms:")) -> "SMS"
        else -> "Text"
    }
}

class ScanActivity : ComponentActivity() {

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scanImageFromUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanScreen(
                onGalleryClick = { galleryLauncher.launch("image/*") },
                onPermissionRequest = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        }
    }

    private fun startCamera() {
        // Camera is started in Compose
    }

    private fun scanImageFromUri(uri: Uri) {
        // Handle in Compose
    }
}

@Composable
fun ScanScreen(
    onGalleryClick: () -> Unit,
    onPermissionRequest: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val storage = remember { HistoryStorage(context) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showCamera by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    var isFlashOn by remember { mutableStateOf(false) }
    var continuousMode by remember { mutableStateOf(false) }
    var scanCount by remember { mutableStateOf(0) }
    var batchResults by remember { mutableStateOf<List<String>>(emptyList()) }

    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    var camera by remember { mutableStateOf<Camera?>(null) }

    DisposableEffect(lifecycleOwner) {
        onDispose { executor.shutdown() }
    }

    fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun saveToHistory(content: String) {
        val type = FormatUtils.detectType(content)
        val format = getFormatName(content)
        val entry = ScanHistory(
            id = System.currentTimeMillis(),
            content = content,
            type = type,
            format = format,
            timestamp = System.currentTimeMillis()
        )
        storage.addEntry(entry)
    }

    fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, BarcodeAnalyzer { result ->
                        if (!showResult && result != null) {
                            vibrate()
                            scanCount++
                            batchResults = batchResults + result

                            if (!continuousMode) {
                                showResult = true
                                lastResult = result
                                showCamera = false
                            } else {
                                lastResult = result
                            }
                            saveToHistory(result)
                        }
                    })
                }
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            camera?.cameraControl?.enableTorch(isFlashOn)
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(showCamera) {
        if (showCamera && hasPermission) {
            startCameraPreview()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Scan Barcode", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (showCamera && hasPermission) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                    )
                    ScanOverlay(
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = {
                                isFlashOn = !isFlashOn
                                camera?.cameraControl?.enableTorch(isFlashOn)
                            }
                        ) {
                            Icon(
                                if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                                contentDescription = "Toggle Flash",
                                tint = Color.White
                            )
                        }
                    }
                } else if (!hasPermission) {
                    com.barcodereader.ui.components.EmptyState(
                        icon = R.drawable.ic_camera_permission,
                        title = "Camera Permission Required",
                        subtitle = "Grant camera access to scan barcodes and QR codes",
                        actionLabel = "Grant Permission",
                        onAction = onPermissionRequest
                    )
                } else {
                    com.barcodereader.ui.components.EmptyState(
                        icon = R.drawable.ic_qr_code,
                        title = "Ready to Scan",
                        subtitle = "Tap the button below to start scanning",
                        actionLabel = "Start Camera",
                        onAction = { showCamera = true }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showCamera) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Continuous Scanning", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = continuousMode,
                            onCheckedChange = { continuousMode = it }
                        )
                    }
                    if (continuousMode && batchResults.isNotEmpty()) {
                        Text(
                            "Scanned: ${batchResults.size} items",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (showCamera) {
                    Button(
                        onClick = { showCamera = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Stop Camera", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    Button(
                        onClick = {
                            if (hasPermission) showCamera = true else onPermissionRequest()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Camera", style = MaterialTheme.typography.labelLarge)
                    }
                }

                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Scan from Gallery", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (showResult && lastResult != null) {
            LoadingOverlay()
        }

        if (showResult && lastResult != null) {
            ResultDialog(
                result = lastResult!!,
                type = FormatUtils.detectType(lastResult!!),
                onDismiss = { showResult = false; lastResult = null },
                onCopy = { FormatUtils.copyToClipboard(context, lastResult!!) },
                onShare = { FormatUtils.shareText(context, lastResult!!) },
                onAction = {
                    when (FormatUtils.detectType(lastResult!!)) {
                        "URL" -> FormatUtils.openInBrowser(context, lastResult!!)
                        else -> Unit
                    }
                },
                actionLabel = when (FormatUtils.detectType(lastResult!!)) {
                    "URL" -> "Open in Browser"
                    else -> ""
                }
            )
        }
    }
}

private class BarcodeAnalyzer(
    private val onDetect: (String?) -> Unit
) : ImageAnalysis.Analyzer {
    private var lastScanTime = 0L

    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastScanTime < 1500) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull()?.rawValue
                if (!value.isNullOrBlank()) {
                    lastScanTime = now
                    onDetect(value)
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}

@Composable
fun ResultDialog(
    result: String,
    type: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onAction: () -> Unit,
    actionLabel: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Scan Result", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(type, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(result, style = MaterialTheme.typography.bodyLarge, maxLines = 8, overflow = TextOverflow.Ellipsis)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Filled.ContentCopy, "Copy")
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Filled.Share, "Share")
                    }
                    if (actionLabel.isNotBlank()) {
                        Button(onClick = onAction, modifier = Modifier.weight(1f)) {
                            Text(actionLabel)
                        }
                    }
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Done")
                    }
                }
            }
        }
    }
}