package com.barcodereader

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.barcodereader.service.ProductInfo
import com.barcodereader.service.ProductLookupService
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val scanResultState = mutableStateOf("Tap the button below to start scanning a barcode or QR code.")
    private val cameraVisibleState = mutableStateOf(false)
    private val productInfoState = mutableStateOf<ProductInfo?>(null)
    private val isLoadingProduct = mutableStateOf(false)

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            scanResultState.value = "Camera permission is required to scan from the camera."
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
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
            BarcodeReaderApp(
                scanResult = scanResultState.value,
                cameraVisible = cameraVisibleState.value,
                productInfo = productInfoState.value,
                isLoadingProduct = isLoadingProduct.value,
                onScanClick = { requestCameraPermission() },
                onGalleryClick = { openGallery() },
                onReset = { 
                    cameraVisibleState.value = false
                    scanResultState.value = "Ready for another scan."
                    productInfoState.value = null
                },
                onBarcodeDetected = { result -> 
                    scanResultState.value = result
                    lookupProduct(result)
                }
            )
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> openCamera()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        cameraVisibleState.value = true
        scanResultState.value = "Camera preview is live. Point at a barcode or QR code."
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun scanImageFromUri(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, uri)
            val scanner = BarcodeScanning.getClient()
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val result = barcodes.firstOrNull()?.rawValue ?: "No barcode found in the selected image."
                    scanResultState.value = result
                    cameraVisibleState.value = false
                    lookupProduct(result)
                }
                .addOnFailureListener {
                    scanResultState.value = "Unable to read a barcode from the selected image."
                    Toast.makeText(this, "Could not read barcode from image", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            scanResultState.value = "Unable to open the selected image."
            Toast.makeText(this, "Unable to open selected image", Toast.LENGTH_LONG).show()
        }
    }

    private fun lookupProduct(barcode: String) {
        val productLookupService = ProductLookupService(this)
        
        if (productLookupService.isProductBarcode(barcode)) {
            isLoadingProduct.value = true
            productInfoState.value = null
            
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                try {
                    val productInfo = productLookupService.lookupProduct(barcode)
                    productInfoState.value = productInfo
                    isLoadingProduct.value = false
                } catch (e: Exception) {
                    isLoadingProduct.value = false
                    Toast.makeText(this@MainActivity, "Failed to lookup product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun BarcodeReaderApp(
    scanResult: String,
    cameraVisible: Boolean,
    productInfo: ProductInfo?,
    isLoadingProduct: Boolean,
    onScanClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onReset: () -> Unit,
    onBarcodeDetected: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Barcode Reader", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Scan product codes and QR codes from the camera or from a saved photo.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            if (cameraVisible) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .background(Color.Black, RoundedCornerShape(20.dp))
                        ) {
                            CameraPreview(onBarcodeDetected = onBarcodeDetected)
                            Box(
                                modifier = Modifier
                                    .size(width = 220.dp, height = 140.dp)
                                    .align(Alignment.Center)
                                    .border(2.dp, Color.White, RoundedCornerShape(18.dp))
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
                            Text("Stop Camera")
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onScanClick, modifier = Modifier.fillMaxWidth()) {
                            Text("Open Camera")
                        }
                        OutlinedButton(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) {
                            Text("Scan from Gallery")
                        }
                    }
                }
            }

            // Scan result card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, colorScheme.outlineVariant)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Latest result", style = MaterialTheme.typography.titleMedium)
                    Text(scanResult, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Product info card (if available)
            if (isLoadingProduct) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Loading product info...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (productInfo != null) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Product Information", style = MaterialTheme.typography.titleMedium)
                        
                        productInfo.name?.let {
                            Text("Name: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        productInfo.brand?.let {
                            Text("Brand: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        productInfo.categories?.let {
                            Text("Categories: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        
                        productInfo.nutriscore?.let {
                            Text("Nutriscore: $it.uppercase()", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(lifecycleOwner) {
        onDispose { executor.shutdown() }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    ) {
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
                    it.setAnalyzer(executor, BarcodeAnalyzer(onBarcodeDetected))
                }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(context))
    }
}

private class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private var lastScanAt = 0L

    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastScanAt < 1800) {
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
                    lastScanAt = now
                    onBarcodeDetected(value)
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}
