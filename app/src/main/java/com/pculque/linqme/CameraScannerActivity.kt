package com.pculque.linqme

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.budiyev.android.codescanner.*
import com.budiyev.android.codescanner.CodeScanner
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build


class CameraScannerActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private val codePermission = 10
    private var mPermissionGranted: Boolean = false
    private val list = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scanner)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val message = it.text
                Toast.makeText(this, "Scan result: $message", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message))
                startActivity(intent)
                startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            runOnUiThread {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        mPermissionGranted = false
                        requestPermissions(list.toTypedArray(), codePermission)
                    } else {
                        mPermissionGranted = true
                    }
                } else {
                    mPermissionGranted = true
                }
            }
        }

        codeScanner.startPreview()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == codePermission) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true
                codeScanner.startPreview()
            } else {
                mPermissionGranted = false

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mPermissionGranted) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}


