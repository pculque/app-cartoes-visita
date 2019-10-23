package com.pculque.linqme.ui.home.adapter

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pculque.linqme.R

class Card {
    var id: Int = 0

    @Expose
    @SerializedName("logo")
    var logo: Int = 0

    @Expose
    @SerializedName("type")
    var type: Int = 0

    @Expose
    @SerializedName("thumbnail")
    var thumbnail: Int = 0
    @Expose
    @SerializedName("backgroundColor")
    var backgroundColor: String = ""

    @Expose
    @SerializedName("labelColor")
    var labelColor: String = ""
    @Expose
    @SerializedName("valueColor")
    var valueColor: String = ""
    @Expose
    @SerializedName("primaryValue")
    var primaryValue: String = ""
    @Expose
    @SerializedName("secondaryLabel")
    var secondaryLabel: String = ""
    @Expose
    @SerializedName("secondaryValue")
    var secondaryValue: String = ""
    @Expose
    @SerializedName("auxiliaryLabel")
    var auxiliaryLabel: String = ""
    @Expose
    @SerializedName("auxiliaryValue")
    var auxiliaryValue: String = ""

    @Expose
    @SerializedName("image")
    var image: String = ""

    var qrCode: String = ""

    fun getTypeId(): TypeCard {
        return when (this.type) {
            1 -> TypeCard.YOUTUBE
            2 -> TypeCard.FACBOOK
            3 -> TypeCard.INSTAGRAM
            4 -> TypeCard.WHATSAPP
            5 -> TypeCard.LINKEDIN
            6 -> TypeCard.BUSSINES
            else -> TypeCard.WHATSAPP
        }
    }

    fun getLogoDrawable(): Int {
        return when (this.type) {
            1 -> R.drawable.logo_youtube
            2 -> R.drawable.logo_facebook
            3 -> R.drawable.logo_instagram
            4 -> R.drawable.logo_whatsapp
            5 -> R.drawable.logo_linkedin
            else -> 0
        }
    }
}