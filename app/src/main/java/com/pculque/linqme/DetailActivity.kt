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
import com.pculque.linqme.database.CardHelper
import java.util.*


class DetailActivity : AppCompatActivity(), ColorPickerDialogListener {
    companion object {
        fun createIntent(
            context: Context
        ): Intent = Intent(context, DetailActivity::class.java).apply {
        }

        // Give your color picker dialog unique IDs if you have multiple dialogs.
        private const val DIALOG_ID = 0
        private var TAG = DetailActivity::class.java.simpleName

    }

    private val codePermission = 10
    private var mPermissionGranted: Boolean = false
    private val dbHandler = CardHelper(this)
    private var isCustomCard = false
    private var images = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id = intent.getIntExtra("card_id", 0)

        val card = dbHandler.getCard(id)

        Log.e(TAG, " ${card?.id}")

        toolbar_detail.title = getString(R.string.app_name)
        toolbar_detail.setNavigationIcon(R.drawable.baseline_close_white_24)
        setSupportActionBar(toolbar_detail)

        if (card != null) {

            findViewById<ConstraintLayout>(R.id.relative_layout).apply {
                setBackgroundColor(Color.parseColor(card.backgroundColor))
            }
            findViewById<TextView>(R.id.title_text_view).apply {
                text = card.primaryValue
                setTextColor(Color.parseColor(card.valueColor))
            }.setOnClickListener {
                showCreateCategoryDialog(card, InputType.TYPE_CLASS_TEXT, title_text_view)
            }
            findViewById<TextView>(R.id.primary_label).apply {
                text = card.secondaryLabel
                setTextColor(Color.parseColor(card.labelColor))
            }
            findViewById<TextView>(R.id.primary_value).apply {
                text = card.secondaryValue
                setTextColor(Color.parseColor(card.valueColor))
            }.setOnClickListener {
                showCreateCategoryDialog(
                    card,
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                    primary_value
                )
            }
            findViewById<TextView>(R.id.auxiliary_label).apply {
                text = card.auxiliaryLabel
                setTextColor(Color.parseColor(card.labelColor))
            }
            findViewById<TextView>(R.id.auxiliary_value).apply {
                text = card.auxiliaryValue
                setTextColor(Color.parseColor(card.valueColor))
            }.setOnClickListener {
                showCreateCategoryDialog(card, InputType.TYPE_CLASS_TEXT, auxiliary_value)
            }
            findViewById<ImageView>(R.id.thumbnail).apply {
                setImageResource(R.drawable.profile)
            }.setOnClickListener {
                val countries = listOf("Trocar foto", "Editar Foto")
                selector("", countries) { _, position ->
                    toast("So you're living in ${countries[position]}, right?")
                    start()
                }
            }
            findViewById<ImageView>(R.id.logo).apply {
                setImageResource(card.getLogoDrawable())
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

                when (card.getTypeId()) {
                    TypeCard.WHATSAPP -> {
                        qrCodeContent =
                            "https://wa.me/5511987854040?text=Olá! Acabamos de nos conhecer através do LINQ.me. Baixe agora o seu."
                        primary_label.text = "Mobile"
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                    }
                    TypeCard.INSTAGRAM -> {
                        primary_label.text = "Follow"
                        qrCodeContent = "https://www.instagram.com/instagram/?hl=pt-br"
                        auxiliaryContainer.visibility = View.INVISIBLE
                    }
                    TypeCard.YOUTUBE -> {
                        qrCodeContent = "https://www.youtube.com/user/YouTube/"
                        primary_label.text = "Canal"
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
                    TypeCard.LINKEDIN -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                        qrCodeContent = "https://www.linkedin.com/in/${card.primaryValue}"
                        //primaryContainer.visibility = View.INVISIBLE
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
        } else {
            toast("Houve um erro ao carregar seus dados")
            finish()
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

    private fun showCreateCategoryDialog(card: Card, inputType: Int, textView: TextView) {
        val context = this
        val builder = AlertDialog.Builder(context)

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
                card.primaryValue = categoryEditText.text.toString()
                textView.text = categoryEditText.text.toString()
                dbHandler.updateCard(card)

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
        if (isCustomCard)
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
            android.R.id.home -> {
                finish()
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


private var toast: Toast? = null

internal fun Activity.toast(message: CharSequence) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }
}


