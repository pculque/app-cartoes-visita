package com.pculque.linqme.ui.home.adapter

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
import com.pculque.linqme.R
import com.pculque.linqme.database.CardHelper
import com.pculque.linqme.util.EncodeUtils
import kotlinx.android.synthetic.main.item_card.view.*

class CardViewHolder private constructor(private val binding: ItemCardBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(
            inflater: LayoutInflater,
            parent: ViewGroup,
            attachToRoot: Boolean
        ): CardViewHolder =
            CardViewHolder(
                ItemCardBinding.inflate(
                    inflater,
                    parent,
                    attachToRoot
                )
            )
    }

    fun bind(
        cardViewModel: CardViewModel,
        onItemClickListener: ((cardView: CardView, cardViewModel: CardViewModel) -> Unit)?
    ) {
        binding.viewModel = cardViewModel
        binding.cardView.setOnClickListener {
            onItemClickListener?.invoke(binding.cardView, cardViewModel)
        }
        val dbHandler = CardHelper(context = binding.cardView.context)
        val card = dbHandler.getCard(cardViewModel.id)

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
                binding.cardView.logo.setImageResource(R.drawable.logo_youtube)
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                qrCodeContent = "https://www.youtube.com/${card?.secondaryValue}"
            }
            TypeCard.WHATSAPP -> {
                binding.cardView.logo.setImageResource(R.drawable.logo_whatsapp)
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                qrCodeContent =
                    "https://wa.me/${card?.secondaryValue}?text=Olá! Acabamos de nos conhecer através do piic.me. Baixe agora o seu."
            }
            TypeCard.FACEBOOK -> {
                binding.cardView.logo.setImageResource(R.drawable.logo_facebook)
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                binding.cardView.secondaryLabel.visibility = View.INVISIBLE
                binding.cardView.secondaryLabel.visibility = View.INVISIBLE
                qrCodeContent = "https://www.facebook.com/${card?.primaryValue}/"
            }
            TypeCard.BUSSINES -> {
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                qrCodeContent = "https://www.instagram.com/daddyyankee/?hl=pt-br"
            }
            TypeCard.INSTAGRAM -> {
                binding.cardView.logo.setImageResource(R.drawable.logo_instagram)
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                qrCodeContent = "https://www.instagram.com/${card?.secondaryValue}/?hl=pt-br"

            }
            TypeCard.LINKEDIN -> {
                binding.cardView.logo.setImageResource(R.drawable.logo_linkedin)
                binding.cardView.auxiliary_label.visibility = View.INVISIBLE
                binding.cardView.auxiliary_value.visibility = View.INVISIBLE
                if (card?.qrCode.isNullOrBlank()) {
                    binding.cardView.img_qr_code.visibility = View.VISIBLE
                    binding.cardView.button_edit_qr.visibility = View.VISIBLE
                } else {
                    qrCodeContent = card?.qrCode!!
                }
                //qrCodeContent = "https://www.linkedin.com/in/${cardViewModel.primaryValue}/"
            }
        }

        if (card != null && card.image.isNotEmpty()) {
            binding.cardView.thumbnail.setImageBitmap(EncodeUtils.decodeFromBase64ToBitmap(card.image))
        } else {
            binding.cardView.thumbnail.setImageResource(R.drawable.profile)
        }
        if (!card?.qrCode.isNullOrBlank()) {
            val qrgEncoder =
                QRGEncoder(qrCodeContent, null, QRGContents.Type.TEXT, smallerDimension)

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
}