package com.bolusbuddy.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bolusbuddy.app.data.ProductLookupResult
import com.bolusbuddy.app.data.ProductRepository
import com.bolusbuddy.app.scanner.BarcodeAnalyzer
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun ScanScreen() {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // The barcode ML Kit found (null = still looking)
    var scannedBarcode by remember { mutableStateOf<String?>(null) }

    // What Open Food Facts said (null = not asked yet, or still waiting)
    var lookupResult by remember { mutableStateOf<ProductLookupResult?>(null) }

    val analyzer = remember { BarcodeAnalyzer { barcode -> scannedBarcode = barcode } }

    // Runs every time scannedBarcode changes
    LaunchedEffect(scannedBarcode) {
        lookupResult = null
        val barcode = scannedBarcode ?: return@LaunchedEffect
        lookupResult = ProductRepository.lookUp(barcode)
    }

    if (hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(modifier = Modifier.fillMaxSize(), analyzer = analyzer)

            val barcode = scannedBarcode
            if (barcode == null) {
                Text(
                    text = "Point the camera at a barcode",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), MaterialTheme.shapes.small)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                ProductPanel(
                    barcode = barcode,
                    result = lookupResult,
                    onScanAgain = {
                        scannedBarcode = null
                        analyzer.reset()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    } else {
        CameraPermissionRequest(
            onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }
}

@Composable
private fun ProductPanel(
    barcode: String,
    result: ProductLookupResult?,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (result) {
                null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(Modifier.size(12.dp))
                        Text("Looking up $barcode…")
                    }
                }

                is ProductLookupResult.Found -> {
                    Text(
                        result.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    result.brand?.let {
                        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    NutrientRow("Carbs per 100 g", formatGrams(result.carbsPer100g))
                    if (result.carbsPerServing != null) {
                        val label = result.servingSize?.let { "Carbs per serving ($it)" }
                            ?: "Carbs per serving"
                        NutrientRow(label, formatGrams(result.carbsPerServing))
                    }
                    if (result.carbsPer100g == null && result.carbsPerServing == null) {
                        Text(
                            "No carb information for this product. Check the packet.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Data from Open Food Facts. Always check against the packet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ProductLookupResult.NotFound -> {
                    Text("Product not found", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Barcode $barcode isn't in Open Food Facts yet. Check the packet and enter the carbs manually.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is ProductLookupResult.Error -> {
                    Text("Couldn't look up product", style = MaterialTheme.typography.titleMedium)
                    Text(result.message, color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(onClick = onScanAgain, modifier = Modifier.fillMaxWidth()) {
                Text("Scan again")
            }
        }
    }
}

@Composable
private fun NutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatGrams(value: Double?): String =
    value?.let { String.format(Locale.UK, "%.1f g", it) } ?: "Not listed"

@Composable
private fun CameraPermissionRequest(onRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Camera access needed", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "BolusBuddy uses the camera to scan food barcodes. Images stay on your phone.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequest) { Text("Allow camera") }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    analyzer: ImageAnalysis.Analyzer? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val analysisExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = analyzer?.let {
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis -> analysis.setAnalyzer(analysisExecutor, it) }
            }

            cameraProvider.unbindAll()
            if (imageAnalysis != null) {
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } else {
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview
                )
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            if (cameraProviderFuture.isDone) cameraProviderFuture.get().unbindAll()
            analysisExecutor.shutdown()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}