package com.pculque.linqme

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pculque.linqme.databinding.ItemCardBinding
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException
import android.util.Log

import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.item_card.view.*

class CardViewHolder private constructor(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(
            inflater: LayoutInflater,
            parent: ViewGroup,
            attachToRoot: Boolean
        ): CardViewHolder = CardViewHolder(ItemCardBinding.inflate(inflater, parent, attachToRoot))
    }

    fun bind(
        cardViewModel: CardViewModel,
        onItemClickListener: ((cardView: CardView, cardViewModel: CardViewModel) -> Unit)?
    ) {
        binding.viewModel = cardViewModel
        binding.cardView.setOnClickListener {
            onItemClickListener?.invoke(binding.cardView, cardViewModel)
        }
        val manager = binding.root.context.getSystemService(WINDOW_SERVICE) as WindowManager?
        val display = manager!!.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4
        var qrCodeContent = ""
        when (cardViewModel.type) {
            TypeCard.YOUTUBE -> {
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                binding.cardView.primary_label.text = "CANAL"
                qrCodeContent = "https://www.youtube.com/channel/UCPl_3zCQnCB4Iv79UD_i-5A"
            }
            TypeCard.WHATSAPP -> {
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                binding.cardView.primary_label.text = "MOBILE"
                qrCodeContent =
                    "https://wa.me/5511987854040?text=Olá! Acabamos de nos conhecer através do LINQ.me. Baixe agora o seu."
            }
            TypeCard.FACBOOK -> {
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                binding.cardView.primary_label.visibility = View.INVISIBLE
                binding.cardView.primary_label.visibility = View.INVISIBLE
                qrCodeContent = "https://www.facebook.com/handsmobile/"
            }
            TypeCard.BUSSINES -> {
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                binding.cardView.primary_label.text = "MOBILE"
                qrCodeContent = "https://www.instagram.com/daddyyankee/?hl=pt-br"
            }
            TypeCard.INSTAGRAM -> {
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                binding.cardView.primary_label.text = "FOLLOW"
                qrCodeContent = "https://www.instagram.com/daddyyankee/?hl=pt-br"

            }

        }
        val qrgEncoder = QRGEncoder(qrCodeContent, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            // Getting QR-Code as Bitmap

            val bitmap = qrgEncoder.encodeAsBitmap()
            // Setting Bitmap to ImageView
            binding.imgQrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.v("CardViewHolder", e.toString())
        }
    }
}