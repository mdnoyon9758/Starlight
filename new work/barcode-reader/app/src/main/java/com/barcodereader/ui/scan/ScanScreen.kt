package com.barcodereader.ui.scan

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.barcodereader.R
import com.barcodereader.data.HistoryStorage
import com.barcodereader.data.ScanHistory
import com.barcodereader.service.ProductInfo
import com.barcodereader.service.ProductLookupService
import com.barcodereader.ui.components.EmptyState
import com.barcodereader.ui.components.ScanResultDisplay
import com.barcodereader.ui.theme.Primary
import com.barcodereader.ui.theme.GlassOverlay
import com.barcodereader.util.FormatUtils
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

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
    val lifecycleOwner = LocalLifecycleOwner.current
    val storage = remember { HistoryStorage(context) }
    val productLookupService = remember { ProductLookupService(context) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showCamera by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<String?>(null) }
    var lastResultType by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var continuousMode by remember { mutableStateOf(true) }
    var scanCount by remember { mutableStateOf(0) }
    var batchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var productInfo by remember { mutableStateOf<ProductInfo?>(null) }
    var isLoadingProduct by remember { mutableStateOf(false) }

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
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun lookupProduct(barcode: String) {
        if (productLookupService.isProductBarcode(barcode)) {
            isLoadingProduct = true
            productInfo = null
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    productInfo = productLookupService.lookupProduct(barcode)
                } catch (e: Exception) { }
                isLoadingProduct = false
            }
        }
    }

    fun saveToHistory(content: String, type: String) {
        val entry = ScanHistory(
            id = System.currentTimeMillis(),
            content = content,
            type = type,
            format = getFormatName(content),
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
                            val type = getFormatName(result)
                            lastResult = result
                            lastResultType = type
                            saveToHistory(result, type)
                            lookupProduct(result)
                            if (!continuousMode) {
                                showResult = true
                                showCamera = false
                            }
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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
                if (showCamera && hasPermission) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                    )
                    AnimatedScanOverlay(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)))
                    
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = {
                                isFlashOn = !isFlashOn
                                camera?.cameraControl?.enableTorch(isFlashOn)
                            },
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(GlassOverlay)
                        ) {
                            Icon(
                                if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                                contentDescription = "Toggle Flash",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    if (continuousMode && scanCount > 0) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopStart) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(GlassOverlay).padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("$scanCount scanned", color = Color.White, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                        Button(
                            onClick = { showCamera = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.9f)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Filled.Stop, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop")
                        }
                    }
                } else if (!hasPermission) {
                    EmptyState(
                        icon = R.drawable.ic_camera_permission,
                        title = "Camera Access Required",
                        subtitle = "Grant camera permission to scan barcodes and QR codes",
                        actionLabel = "Grant Permission",
                        onAction = onPermissionRequest
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(120.dp).clip(CircleShape).background(Primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Primary, modifier = Modifier.size(64.dp))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Ready to Scan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Point your camera at a barcode or QR code",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Continuous Scanning", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = continuousMode,
                        onCheckedChange = { continuousMode = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                    )
                }
                
                if (!showCamera) {
                    Button(
                        onClick = { if (hasPermission) showCamera = true else onPermissionRequest() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Start Scanning", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
        
        AnimatedVisibility(
            visible = showResult && lastResult != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { showResult = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ScanResultDisplay(
                        result = lastResult!!,
                        type = lastResultType ?: "TEXT",
                        productInfo = productInfo,
                        onCopy = { FormatUtils.copyToClipboard(context, lastResult!!) },
                        onShare = { FormatUtils.shareText(context, lastResult!!) },
                        onDismiss = { showResult = false }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { showResult = false },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
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

@Composable
fun AnimatedScanOverlay(
    modifier: Modifier = Modifier,
    lineColor: Color = Primary,
    cornerColor: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(modifier = modifier.background(Color.Black.copy(alpha = 0.4f))) {
        Box(
            modifier = Modifier.size(250.dp, 180.dp).align(Alignment.Center)
        ) {
            ScanCornerBox(Modifier.align(Alignment.TopStart), cornerColor)
            ScanCornerBox(Modifier.align(Alignment.TopEnd), cornerColor)
            ScanCornerBox(Modifier.align(Alignment.BottomStart), cornerColor)
            ScanCornerBox(Modifier.align(Alignment.BottomEnd), cornerColor)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.CenterStart)
                    .graphicsLayer { translationY = (scanLineY - 0.5f) * 180f }
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, lineColor, lineColor, Color.Transparent)
                        )
                    )
            )
        }
    }
}

@Composable
private fun ScanCornerBox(modifier: Modifier = Modifier, color: Color = Color.White) {
    Box(modifier = modifier.size(32.dp)) {
        Box(modifier = Modifier.width(24.dp).height(3.dp).background(color).align(Alignment.TopStart))
        Box(modifier = Modifier.width(3.dp).height(24.dp).background(color).align(Alignment.TopStart))
    }
}

private class BarcodeAnalyzer(private val onDetect: (String?) -> Unit) : ImageAnalysis.Analyzer {
    private var lastScanTime = 0L
    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastScanTime < 1000) { imageProxy.close(); return }
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull()?.rawValue
                if (!value.isNullOrBlank()) { lastScanTime = now; onDetect(value) }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}
