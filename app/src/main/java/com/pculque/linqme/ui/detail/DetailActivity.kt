package com.pculque.linqme.ui.detail

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import br.com.concrete.canarinho.watcher.MascaraNumericaTextWatcher

import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.pculque.linqme.ui.home.adapter.Card
import com.pculque.linqme.R
import com.pculque.linqme.ui.home.adapter.TypeCard
import com.pculque.linqme.database.CardHelper
import com.pculque.linqme.util.EncodeUtils
import java.util.*

import id.zelory.compressor.Compressor

import java.io.File

class DetailActivity : AppCompatActivity(), ColorPickerDialogListener {
    companion object {
        fun createIntent(
            context: Context
        ): Intent = Intent(context, DetailActivity::class.java).apply {
        }

        // Give your color picker dialog unique IDs if you have multiple dialogs.
        private const val DIALOG_ID = 0
        private var TAG = DetailActivity::class.java.simpleName
        private var cardId: Int = 0

    }

    private val codePermission = 10
    private var mPermissionGranted: Boolean = false
    private val dbHandler = CardHelper(this)
    private var isCustomCard = false
    private var images = ArrayList<Image>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        cardId = intent.getIntExtra("card_id", 0)

        val card = dbHandler.getCard(cardId)

        if (card != null) {
            toolbar_detail.title = getTitleByTypeCard(card = card)
            toolbar_detail.setNavigationIcon(R.drawable.baseline_close_white_24)
            setSupportActionBar(toolbar_detail)

            if (card.image.isNotEmpty()) {
                thumbnail.setImageBitmap(EncodeUtils.decodeFromBase64ToBitmap(card.image))
            } else {
                thumbnail.setImageResource(R.drawable.profile)
            }

            findViewById<ConstraintLayout>(R.id.relative_layout).apply {
                setBackgroundColor(Color.parseColor(card.backgroundColor))
            }
            findViewById<TextView>(R.id.primaryValue).apply {
                text = card.primaryValue
                setTextColor(Color.parseColor(card.valueColor))
            }.setOnClickListener {
                showDialog(
                    loadText = card.primaryValue,
                    card = card,
                    textView = primaryValue,
                    typeField = TypeField.PRIMARY
                )
            }
            findViewById<TextView>(R.id.secondaryLabel).apply {
                text = card.secondaryLabel
                setTextColor(Color.parseColor(card.labelColor))
            }
            if (card.getTypeId() == TypeCard.FACBOOK) {
                button_edit_qr.visibility = View.VISIBLE
            }
            button_edit_qr.setOnClickListener {
                showDialog(
                    loadText = getQRCode(card),
                    card = card,
                    typeField = TypeField.QR_CODE,
                    textView = null
                )
            }
            secondaryLabel.setOnClickListener {
                if (card.getTypeId() == TypeCard.LINKEDIN) {
                    showDialog(
                        loadText = card.secondaryLabel,
                        card = card,
                        textView = secondaryLabel,
                        typeField = TypeField.SECONDARY_LABEL
                    )
                }
            }
            findViewById<TextView>(R.id.secondaryValue).apply {
                text = card.secondaryValue
                setTextColor(Color.parseColor(card.valueColor))
            }.setOnClickListener {
                showDialog(
                    loadText = card.secondaryValue,
                    card = card,
                    textView = secondaryValue,
                    typeField = TypeField.SECONDARY
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
                showDialog(
                    loadText = card.auxiliaryValue,
                    card = card,
                    textView = auxiliary_value,
                    typeField = TypeField.AUXILIARY
                )
            }

            thumbnail.setOnClickListener {
                val countries = listOf("Trocar foto", "Editar Foto")
                selector("", countries) { _, _ ->
                    start()
                }
            }

            findViewById<ImageView>(R.id.logo).apply {
                setImageResource(card.getLogoDrawable())
            }
            findViewById<ImageView>(R.id.img_qr_code).apply {
                val smallerDimension = getSmallDimension()

                when (card.getTypeId()) {
                    TypeCard.WHATSAPP -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                    }
                    TypeCard.INSTAGRAM -> {
                        auxiliaryContainer.visibility = View.INVISIBLE
                    }
                    TypeCard.YOUTUBE -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE

                    }
                    TypeCard.BUSSINES -> {
                    }
                    TypeCard.FACBOOK -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                        primaryContainer.visibility = View.INVISIBLE
                    }
                    TypeCard.LINKEDIN -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                    }
                }
                if (card.getTypeId() == TypeCard.FACBOOK) {
                    updateQRCode(card = card, smallerDimension = smallerDimension)
                } else {
                    updateQRCode(card = card, smallerDimension = smallerDimension)
                }

            }
        } else {
            toast("Houve um erro ao carregar seus dados")
            finish()
        }
    }

    private fun getTitleByTypeCard(card: Card): String {
        return when (card.getTypeId()) {
            TypeCard.YOUTUBE -> "YouTube Card"
            TypeCard.WHATSAPP -> "WhatsApp Card"
            TypeCard.INSTAGRAM -> "Instagram Card"
            TypeCard.FACBOOK -> "Facebook Card"
            TypeCard.LINKEDIN -> "LinkedIn Card"
            TypeCard.BUSSINES -> ""
        }
    }

    private fun getQRCode(card: Card): String {
        return when (card.getTypeId()) {
            TypeCard.YOUTUBE -> "https://www.youtube.com/user/${card.secondaryValue}/"
            TypeCard.WHATSAPP -> "https://wa.me/55${card.secondaryValue.replace(
                "-",
                ""
            ).replace(" ", "")}?text=${getString(R.string.text_whatsapp)}"
            TypeCard.INSTAGRAM -> "https://www.instagram.com/${card.secondaryValue.replace(
                "@",
                ""
            )}/?hl=pt-br"
            TypeCard.FACBOOK -> "https://www.facebook.com/${card.primaryValue}"
            TypeCard.LINKEDIN -> "https://www.linkedin.com/in/${card.primaryValue}"
            TypeCard.BUSSINES -> ""
        }
    }

    private fun getSmallDimension(): Int {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val display = manager!!.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4
        return smallerDimension
    }

    private fun ImageView.updateQRCode(
        card: Card,
        smallerDimension: Int
    ) {
        val qrCodeContent: String

        if (card.getTypeId() == TypeCard.FACBOOK) {
            qrCodeContent = "https://www.facebook.com/seu-nome-aqui"
        } else {
            qrCodeContent = getQRCode(card)
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
                val file = File(images.first().path)
                val imageBitmap = customCompressImage(file)
                //setImageURI(Uri.parse(images.first().path))
                val string64 = EncodeUtils.convertToBase64(imageBitmap)
                Log.e(TAG, "Id: $cardId")
                val card = dbHandler.getCard(cardId)
                if (card != null) {
                    card.image = string64
                    dbHandler.updateCard(card)
                } else {
                    toast("Houve um erro ao salvar imagem")
                }
                setImageBitmap(imageBitmap)
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun customCompressImage(imageFile: File): Bitmap {
        return Compressor(this)
            .setMaxWidth(512)
            .setMaxHeight(512)
            .setQuality(100)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setDestinationDirectoryPath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).absolutePath
            )
            .compressToBitmap(imageFile)
    }

    private fun showDialog(
        loadText: String,
        card: Card,
        textView: TextView?,
        typeField: TypeField
    ) {
        val context = this
        val builder = AlertDialog.Builder(context)

        // Seems ok to inflate view with null rootView
        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val categoryEditText = view.findViewById(R.id.categoryEditText) as EditText
        categoryEditText.setText(loadText)
        categoryEditText.inputType =
            getInputType(typeCard = card.getTypeId(), typeField = typeField)

        if (card.getTypeId() == TypeCard.WHATSAPP && typeField == TypeField.SECONDARY) {
            categoryEditText.addTextChangedListener(MascaraNumericaTextWatcher("##-#########"))
        }

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val newCategory = categoryEditText.text
            var isValid = true
            if (newCategory.isBlank()) {
                categoryEditText.error = getString(R.string.app_name)
                isValid = false
            }

            if (isValid) {

                when (typeField) {
                    TypeField.PRIMARY -> {
                        card.primaryValue = categoryEditText.text.toString()
                        textView?.text = categoryEditText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.SECONDARY -> {
                        card.secondaryValue = categoryEditText.text.toString()
                        textView?.text = categoryEditText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.AUXILIARY -> {
                        card.auxiliaryValue = categoryEditText.text.toString()
                        textView?.text = categoryEditText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.SECONDARY_LABEL -> {
                        card.secondaryLabel = categoryEditText.text.toString()
                        textView?.text = categoryEditText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.QR_CODE -> {
                        getQRCode(card)
                    }
                }
                img_qr_code.updateQRCode(
                    card = card,
                    smallerDimension = getSmallDimension()
                )
            }

            if (isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun getInputType(typeCard: TypeCard, typeField: TypeField): Int {
        return if (typeCard == TypeCard.WHATSAPP && typeField == TypeField.PRIMARY) {
            InputType.TYPE_CLASS_TEXT
        } else if (typeCard == TypeCard.WHATSAPP && typeField == TypeField.SECONDARY) {
            InputType.TYPE_CLASS_NUMBER
        } else InputType.TYPE_CLASS_TEXT
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

enum class TypeField {
    PRIMARY, SECONDARY, SECONDARY_LABEL, AUXILIARY, QR_CODE
}


