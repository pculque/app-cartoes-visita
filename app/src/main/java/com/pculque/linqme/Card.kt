package com.pculque.linqme

class Card {
    var id: Int = 0

    var logo: Int = 0
    var thumbnail: Int = 0
    var backgroundColor: Int = 0
    var labelColor: Int = 0
    var valueColor: Int = 0
    var primaryValue: String = ""
    var secondaryLabel: String = ""
    var secondaryValue: String = ""
    var auxiliaryLabel: String = ""
    var auxiliaryValue: String = ""

    constructor(id: Int, primaryValue: String) {
        this.id = id
        this.primaryValue = primaryValue
    }

    constructor(primaryValue: String) {
        this.primaryValue = primaryValue
    }

    constructor(
        logo: Int,
        thumbnail: Int,
        backgroundColor: Int,
        labelColor: Int,
        valueColor: Int,
        primaryValue: String,
        secondaryLabel: String,
        secondaryValue: String,
        auxiliaryLabel: String,
        auxiliaryValue: String
    ) {
        this.logo = logo
        this.thumbnail = thumbnail
        this.backgroundColor = backgroundColor
        this.labelColor = labelColor
        this.valueColor = valueColor
        this.primaryValue = primaryValue
        this.secondaryLabel = secondaryLabel
        this.secondaryValue = secondaryValue
        this.auxiliaryLabel = auxiliaryLabel
        this.auxiliaryValue = auxiliaryValue
    }


}