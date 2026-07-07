package com.barcodereader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class MainActivity : ComponentActivity() {
    private var scannedText by mutableStateOf("Scan a barcode or QR code")

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to scan barcodes", Toast.LENGTH_LONG).show()
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
                statusText = scannedText,
                onScanClick = { requestCameraPermission() },
                onGalleryClick = { openGallery() }
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
        scannedText = "Camera preview is ready. Point at a barcode or QR code."
        Toast.makeText(this, "Camera preview is ready", Toast.LENGTH_SHORT).show()
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
                    val result = barcodes.firstOrNull()?.rawValue ?: "No barcode found in selected image"
                    scannedText = result
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    scannedText = "Failed to scan selected image"
                    Toast.makeText(this, "Could not read barcode from image", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            scannedText = "Unable to open selected image"
            Toast.makeText(this, "Unable to open selected image", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun BarcodeReaderApp(statusText: String, onScanClick: () -> Unit, onGalleryClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Barcode Reader", style = MaterialTheme.typography.headlineMedium)
            Text("Scan barcodes or QR codes from the camera or a gallery photo", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
            Button(onClick = onScanClick) {
                Text("Open Camera")
            }
            OutlinedButton(onClick = onGalleryClick, modifier = Modifier.padding(top = 12.dp)) {
                Text("Scan from Gallery")
            }
            Text(statusText, modifier = Modifier.padding(top = 24.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
