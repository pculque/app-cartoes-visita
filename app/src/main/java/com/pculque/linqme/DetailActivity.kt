package com.pculque.linqme

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.text.InputType
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.selector
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import com.esafirm.imagepicker.features.ReturnMode
import android.graphics.Color
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat

import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import java.lang.reflect.Type
import java.util.*


class DetailActivity : AppCompatActivity(), ColorPickerDialogListener {
    companion object {
        fun createIntent(
            context: Context,
            primaryValue: String,
            secondaryLabel: String,
            secondaryValue: String,
            auxiliaryLabel: String,
            auxiliaryValue: String,
            background: Int,
            labelColor: Int,
            valueColor: Int,
            logo: Int,
            thumbnail: Int
        ): Intent = Intent(context, DetailActivity::class.java).apply {
            this["primaryValue"] = primaryValue
            this["secondaryLabel"] = secondaryLabel
            this["secondaryValue"] = secondaryValue
            this["auxiliaryLabel"] = auxiliaryLabel
            this["auxiliaryValue"] = auxiliaryValue
            this["backgroundColor"] = background
            this["labelColor"] = labelColor
            this["valueColor"] = valueColor
            this["logo"] = logo
            this["thumbnail"] = thumbnail

        }
    }

    private val title: String by getStringExtra("primaryValue")
    private val secondaryLabel: String by getStringExtra("secondaryLabel")
    private val secondaryValue: String by getStringExtra("secondaryValue")

    private val auxiliaryLabel: String by getStringExtra("auxiliaryLabel")
    private val auxiliaryValue: String by getStringExtra("auxiliaryValue")

    private val background: Int by getIntExtra("backgroundColor")
    private val labelColor: Int by getIntExtra("labelColor")
    private val valueColor: Int by getIntExtra("valueColor")

    private val logo: Int by getIntExtra("logo")
    private val thumbnail: Int by getIntExtra("thumbnail")
    private val codePermission = 10
    private var mPermissionGranted: Boolean = false
    private var TAG: String = "Detail"

    // Give your color picker dialog unique IDs if you have multiple dialogs.
    private val DIALOG_ID = 0

    private var images = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar_detail)
        findViewById<ConstraintLayout>(R.id.relative_layout).apply {
            setBackgroundColor(this@DetailActivity.background)
        }
        findViewById<TextView>(R.id.title_text_view).apply {
            text = title
            setTextColor(valueColor)
        }.setOnClickListener {
            showCreateCategoryDialog(InputType.TYPE_CLASS_TEXT, title_text_view)
        }
        findViewById<TextView>(R.id.primary_label).apply {
            text = secondaryLabel
            setTextColor(labelColor)
        }
        findViewById<TextView>(R.id.primary_value).apply {
            text = secondaryValue
            setTextColor(valueColor)
        }.setOnClickListener {
            showCreateCategoryDialog(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, primary_value)
        }
        findViewById<TextView>(R.id.auxiliary_label).apply {
            text = auxiliaryLabel
            setTextColor(valueColor)
        }
        findViewById<TextView>(R.id.auxiliary_value).apply {
            text = auxiliaryValue
            setTextColor(valueColor)
        }.setOnClickListener {
            showCreateCategoryDialog(InputType.TYPE_CLASS_TEXT, auxiliary_value)
        }
        findViewById<ImageView>(R.id.thumbnail).apply {
            setImageResource(thumbnail)
        }.setOnClickListener {
            val countries = listOf("Trocar foto", "Editar Foto")
            selector("", countries) { _, position ->
                toast("So you're living in ${countries[position]}, right?")
                start()
            }
        }
        findViewById<ImageView>(R.id.logo).apply {
            setImageResource(logo)
        }
        findViewById<ImageView>(R.id.img_qr_code).apply {
            val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            val display = manager!!.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var smallerDimension = if (width < height) width else height
            smallerDimension = smallerDimension * 3 / 4
            var qrCodeContent = ""

            when (intent.getSerializableExtra("type") as TypeCard) {
                TypeCard.WHATSAPP -> {
                    qrCodeContent = "https://wa.me/5511987854040?text=Olá! Acabamos de nos conhecer através do LINQ.me. Baixe agora o seu."
                    primary_label.text = "MOBILE"
                    auxiliary_label.visibility = View.INVISIBLE
                    auxiliary_value.visibility = View.INVISIBLE
                }
                TypeCard.INSTAGRAM -> {
                    primary_label.text = "FOLLOW"
                    qrCodeContent = "https://www.instagram.com/instagram/?hl=pt-br"
                    auxiliaryContainer.visibility = View.INVISIBLE
                }
                TypeCard.YOUTUBE -> {
                    qrCodeContent = "https://www.youtube.com/user/YouTube/"
                    primary_label.text = "CHANNEL"
                    auxiliary_label.visibility = View.INVISIBLE
                    auxiliary_value.visibility = View.INVISIBLE

                }
                TypeCard.BUSSINES -> {
                    qrCodeContent = "https://www.instagram.com/daddyyankee/?hl=pt-br"

                }
                TypeCard.FACBOOK -> {
                    auxiliary_label.visibility = View.INVISIBLE
                    auxiliary_value.visibility = View.INVISIBLE
                    qrCodeContent = "https://www.facebook.com/zuck"
                    primaryContainer.visibility = View.INVISIBLE
                }
            }

            val qrgEncoder =
                QRGEncoder(qrCodeContent, null, QRGContents.Type.TEXT, smallerDimension)

            try {
                // Getting QR-Code as Bitmap
                val bitmap = qrgEncoder.encodeAsBitmap()
                // Setting Bitmap to ImageView
                setImageBitmap(bitmap)
            } catch (e: WriterException) {
                Log.v("CardViewHolder", e.toString())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == codePermission) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true
                start()
            } else {
                mPermissionGranted = false

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = ImagePicker.getImages(data) as ArrayList<Image>

            findViewById<ImageView>(R.id.thumbnail).apply {
                setImageURI(Uri.parse(images.first().path))
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showCreateCategoryDialog(inputType: Int, textView: TextView) {
        val context = this
        val builder = AlertDialog.Builder(context)

        // https://stackoverflow.com/questions/10695103/creating-custom-alertdialog-what-is-the-root-view
        // Seems ok to inflate view with null rootView
        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val categoryEditText = view.findViewById(R.id.categoryEditText) as EditText
        categoryEditText.inputType = inputType
        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val newCategory = categoryEditText.text
            var isValid = true
            if (newCategory.isBlank()) {
                categoryEditText.error = getString(R.string.app_name)
                isValid = false
            }

            if (isValid) {
                // do something
                textView.text = categoryEditText.text.toString()
            }

            if (isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_picker -> {
                ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                    .setAllowPresets(false)
                    .setDialogId(DIALOG_ID)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setShowAlphaSlider(true)
                    .show(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        Log.d(TAG, "onColorSelected() called with: dialogId = [$dialogId], color = [$color]")
        when (dialogId) {
            DIALOG_ID -> {
                findViewById<ConstraintLayout>(R.id.relative_layout).apply {
                    setBackgroundColor(color)
                }
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {
        Log.d(TAG, "onDialogDismissed() called with: dialogId = [$dialogId]")
    }

    private fun getImagePicker(): ImagePicker {
        val imagePicker = ImagePicker.create(this)
            .language(Locale.getDefault().language) // Set image picker language
            .theme(R.style.ImagePickerTheme)
            .returnMode(ReturnMode.NONE) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            .toolbarArrowColor(Color.WHITE) // set toolbar arrow up color
            .toolbarFolderTitle("Galeria") // folder selection title
            .toolbarImageTitle("Click para selecionar") // image selection title
        //.toolbarDoneButtonText("DONE") // done button text

        return imagePicker.limit(1) // max images can be selected (99 by default)
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
    }

    private fun start() {
        getImagePicker().start() // start image picker activity with request code
    }


}


fun getStringExtra(extra: String): ReadOnlyProperty<AppCompatActivity, String> =
    object : ReadOnlyProperty<AppCompatActivity, String> {
        override fun getValue(
            thisRef: AppCompatActivity,
            property: KProperty<*>
        ): String = thisRef.intent.getStringExtra(extra)
    }

fun getIntExtra(extra: String): ReadOnlyProperty<AppCompatActivity, Int> =
    object : ReadOnlyProperty<AppCompatActivity, Int> {
        override fun getValue(
            thisRef: AppCompatActivity,
            property: KProperty<*>
        ): Int = thisRef.intent.getIntExtra(extra, 0)
    }

operator fun Intent.set(name: String, value: String) {
    putExtra(name, value)
}

operator fun Intent.set(name: String, value: Int) {
    putExtra(name, value)
}

private var toast: Toast? = null

internal fun Activity.toast(message: CharSequence) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }
}


