package com.pculque.linqme.ui.scanner

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.budiyev.android.codescanner.*
import com.budiyev.android.codescanner.CodeScanner
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.pculque.linqme.R
import android.content.ActivityNotFoundException
 import android.util.Log
import com.pculque.linqme.ui.detail.toast


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

                try {
                    // Your startActivity code wich throws exception
                    val message = it.text.trim()
                    Log.e("LEITOR", message.trim())

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message))
                    intent.type = "text/vcard"
                    startActivity(intent)
                    finish()
                    //val intent = Intent(Intent.ACTION_VIEW)
                    //val shareUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, message)
                    //intent.type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
                    // intent.putExtra(Intent.EXTRA_STREAM, shareUri)
                    //startActivity(intent)
                } catch (activityNotFound: ActivityNotFoundException) {
                    // Now, You can catch the exception here and do what you want
                    toast("Houve um erro ao ler seu qr code :c ")
                }

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


