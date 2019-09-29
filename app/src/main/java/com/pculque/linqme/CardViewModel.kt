package com.pculque.linqme

import androidx.lifecycle.LiveData


data class CardViewModel(
    val logo: LiveData<Int>,
    val thumbnail: LiveData<Int>,
    val backgroundColor: LiveData<Int>,
    val labelColor: LiveData<Int>,
    val valueColor: LiveData<Int>,
    val primaryValue: LiveData<String>,
    val secondaryLabel: LiveData<String>,
    val secondaryValue: LiveData<String>,
    val auxiliaryLabel: LiveData<String>,
    val auxiliaryValue: LiveData<String>,
    val type: TypeCard,
    val id: Int
)