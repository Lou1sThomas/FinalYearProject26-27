package com.bolusbuddy.app.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Receives camera frames from CameraX and uses ML Kit to look for
 * food barcodes. Calls onBarcodeFound once per new barcode.
 */
class BarcodeAnalyzer(
    private val onBarcodeFound: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Only look for the barcode types printed on food packaging
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E
            )
            .build()
    )

    // Stops the same barcode firing 30 times a second
    private var lastBarcode: String? = null

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull()?.rawValue
                if (value != null && value != lastBarcode) {
                    lastBarcode = value
                    onBarcodeFound(value)
                }
            }
            .addOnCompleteListener {
                // Must always close the frame, or CameraX stops sending new ones
                imageProxy.close()
            }
    }

    /** Lets the same product be scanned again. */
    fun reset() {
        lastBarcode = null
    }
}