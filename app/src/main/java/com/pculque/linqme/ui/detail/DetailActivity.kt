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
import com.pculque.linqme.data.CardHelper
import com.pculque.linqme.ui.home.MainActivity
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
    private var startActivity: Boolean = true


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
            if (card.getTypeId() == TypeCard.FACEBOOK || card.getTypeId() == TypeCard.LINKEDIN) {
                button_edit_qr.visibility = View.VISIBLE
            } else {
                button_edit_qr.visibility = View.GONE
            }
            button_edit_qr.setOnClickListener {
                showDialogCustomQR(
                    card = card,
                    typeField = TypeField.QR_CODE
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

            if ((card.getTypeId() == TypeCard.YOUTUBE
                        || card.getTypeId() == TypeCard.FACEBOOK
                        || card.getTypeId() == TypeCard.INSTAGRAM
                        || card.getTypeId() == TypeCard.WHATSAPP
                        || card.getTypeId() == TypeCard.LINKEDIN) == card.primaryValue.isEmpty()
            ) {
                primaryValue.text = "Digite o seu Nome"
            }
            if (card.getTypeId() == TypeCard.YOUTUBE && card.secondaryValue.isEmpty()) {
                secondaryValue.text = "Digite o ID do seu canal"
            }
            if (card.getTypeId() == TypeCard.INSTAGRAM && card.secondaryValue.isEmpty()) {
                secondaryValue.text = "Digite o ID da sua conta (@meu_id)"
            }
            if (card.getTypeId() == TypeCard.WHATSAPP && card.secondaryValue.isEmpty()) {
                secondaryValue.text = "Digite o seu número"
            }
            if (card.getTypeId() == TypeCard.LINKEDIN && card.secondaryValue.isEmpty()) {
                secondaryValue.text = "Digite o seu cargo"
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
                    TypeCard.FACEBOOK -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                        primaryContainer.visibility = View.INVISIBLE
                    }
                    TypeCard.LINKEDIN -> {
                        auxiliary_label.visibility = View.INVISIBLE
                        auxiliary_value.visibility = View.INVISIBLE
                    }
                }
                img_qr_code.setOnClickListener {
                    showDialogCustomQR(
                        card = card, typeField = TypeField.QR_CODE
                    )
                }

                if ((card.getTypeId() == TypeCard.FACEBOOK || card.getTypeId() == TypeCard.LINKEDIN) && card.qrCode.isEmpty()) {

                } else {
                    updateQRCode(
                        card = card,
                        smallerDimension = smallerDimension,
                        typeField = TypeField.PRIMARY
                    )
                }

            }
        } else {
            toast("Houve um erro ao carregar seus dados")
            finish()
        }
        startActivity = false
    }

    private fun getTitleByTypeCard(card: Card): String {
        return when (card.getTypeId()) {
            TypeCard.YOUTUBE -> "YouTube Card"
            TypeCard.WHATSAPP -> "WhatsApp Card"
            TypeCard.INSTAGRAM -> "Instagram Card"
            TypeCard.FACEBOOK -> "Facebook Card"
            TypeCard.LINKEDIN -> "LinkedIn Card"
            TypeCard.BUSSINES -> ""
        }
    }

    private fun getQRCode(card: Card): String {
        return when (card.getTypeId()) {
            TypeCard.YOUTUBE -> "https://www.youtube.com/${card.secondaryValue}/"
            //TypeCard.YOUTUBE -> card.qrCode
            TypeCard.WHATSAPP -> "https://wa.me/55${card.secondaryValue
                .replace(
                    "(", ""
                ).replace(")", "")
                .replace(" ", "")
                .replace("-", "")}?text=${getString(R.string.text_whatsapp)}"
            TypeCard.INSTAGRAM -> "https://www.instagram.com/${card.secondaryValue.replace(
                "@",
                ""
            )}/?hl=pt-br"
            TypeCard.FACEBOOK -> card.qrCode
            //TypeCard.LINKEDIN -> "https://www.linkedin.com/in/${card.primaryValue}"
            TypeCard.LINKEDIN -> card.qrCode
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
        smallerDimension: Int, typeField: TypeField

    ) {
        val qrCodeContent: String

        if ((card.getTypeId() == TypeCard.FACEBOOK || card.getTypeId() == TypeCard.LINKEDIN) && card.qrCode.isNotEmpty()) {
            button_edit_qr.visibility = View.GONE
            qrCodeContent = card.qrCode
        } else {
            qrCodeContent = getQRCode(card)
        }

        if (!startActivity && typeField == TypeField.QR_CODE)
            toast(qrCodeContent)

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

    private fun showDialogCustomQR(
        card: Card,
        typeField: TypeField
    ) {

        val context = this
        val builder = AlertDialog.Builder(context)

        // Seems ok to inflate view with null rootView
        val view = layoutInflater.inflate(R.layout.dialog_new_category, null)

        val inputText = view.findViewById(R.id.input_text) as EditText

        if (card.getTypeId() == TypeCard.FACEBOOK || card.getTypeId() == TypeCard.LINKEDIN) {
            inputText.hint = "Cole a URL do seu perfil aqui..."
        }
        if (card.qrCode.isNotEmpty()) {
            inputText.setText(card.qrCode)
        }

        inputText.inputType =
            InputType.TYPE_CLASS_TEXT

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val newCategory = inputText.text
            var isValid = true
            if (newCategory.isBlank()) {
                inputText.error = getString(R.string.app_name)
                isValid = false
            }

            if (isValid) {
                card.qrCode = inputText.text.toString()
                dbHandler.updateCard(card)
                img_qr_code.updateQRCode(
                    card = card,
                    smallerDimension = getSmallDimension(),
                    typeField = typeField
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

        val inputValueText = view.findViewById(R.id.input_text) as EditText

        inputValueText.inputType =
            getInputType(typeCard = card.getTypeId(), typeField = typeField)


        if ((card.getTypeId() == TypeCard.YOUTUBE || card.getTypeId() == TypeCard.FACEBOOK || card.getTypeId() == TypeCard.LINKEDIN || card.getTypeId() == TypeCard.INSTAGRAM || card.getTypeId() == TypeCard.LINKEDIN) && typeField == TypeField.PRIMARY && card.primaryValue.isEmpty()) {
            inputValueText.hint = "Digite o seu Nome..."
        } else if (card.getTypeId() == TypeCard.YOUTUBE && typeField == TypeField.SECONDARY && card.secondaryValue.isEmpty()) {
            inputValueText.hint = "Digite o ID do seu canal..."
        } else if (card.getTypeId() == TypeCard.INSTAGRAM && typeField == TypeField.SECONDARY && card.secondaryValue.isEmpty()) {
            inputValueText.hint = "Digite o ID da sua conta (@meu_id)..."
        } else if (card.getTypeId() == TypeCard.WHATSAPP && typeField == TypeField.SECONDARY && card.secondaryValue.isEmpty()) {
            inputValueText.hint = "Digite o seu número..."
        } else if (card.getTypeId() == TypeCard.LINKEDIN && typeField == TypeField.SECONDARY && card.secondaryValue.isEmpty()) {
            inputValueText.hint = "Digite o seu cargo..."
        } else if (card.getTypeId() == TypeCard.LINKEDIN && typeField == TypeField.SECONDARY_LABEL) {
            inputValueText.hint = "Digite o nome da sua empresa..."
        } else {
            inputValueText.setText(loadText)
        }

        if (card.getTypeId() == TypeCard.WHATSAPP && typeField == TypeField.SECONDARY) {
            inputValueText.addTextChangedListener(MascaraNumericaTextWatcher("(##) #####-####"))
        }

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val newCategory = inputValueText.text
            var isValid = true
            if (newCategory.isBlank()) {
                inputValueText.error = getString(R.string.app_name)
                isValid = false
            }

            if (isValid) {

                when (typeField) {
                    TypeField.PRIMARY -> {
                        card.primaryValue = inputValueText.text.toString()
                        textView?.text = inputValueText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.SECONDARY -> {
                        card.secondaryValue = inputValueText.text.toString()
                        textView?.text = inputValueText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.AUXILIARY -> {
                        card.auxiliaryValue = inputValueText.text.toString()
                        textView?.text = inputValueText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.SECONDARY_LABEL -> {
                        card.secondaryLabel = inputValueText.text.toString()
                        textView?.text = inputValueText.text.toString()
                        dbHandler.updateCard(card)
                    }
                    TypeField.QR_CODE -> {
                        getQRCode(card)
                    }
                }
                if (card.getTypeId() != TypeCard.FACEBOOK && card.getTypeId() != TypeCard.LINKEDIN) {
                    img_qr_code.updateQRCode(
                        card = card,
                        smallerDimension = getSmallDimension(), typeField = typeField
                    )
                }
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
                setResult(MainActivity.RESULT_CODE, intent)
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


