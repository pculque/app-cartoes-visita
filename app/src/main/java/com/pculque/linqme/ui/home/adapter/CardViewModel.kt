package com.pculque.linqme.ui.home.adapter

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.pculque.linqme.ui.home.adapter.TypeCard


data class CardViewModel(
    val logo: LiveData<Int>,
    val backgroundColor: LiveData<Int>,
    val labelColor: LiveData<Int>,
    val valueColor: LiveData<Int>,
    val primaryValue: LiveData<String>,
    val secondaryLabel: LiveData<String>,
    val secondaryValue: LiveData<String>,
    val auxiliaryLabel: LiveData<String>,
    val auxiliaryValue: LiveData<String>,
    val bitmap: LiveData<Bitmap>,

    val type: TypeCard,
    val id: Int
)