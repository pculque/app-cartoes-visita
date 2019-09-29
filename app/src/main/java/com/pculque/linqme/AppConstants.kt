package com.pculque.linqme


object AppConstants {

    internal const val APP_DB_NAME = "mindorks_mvp.db"
    internal const val PREF_NAME = "mindorks_pref"
    internal const val SEED_DATABASE_QUESTIONS = "seed/cards.json"


    enum class LoggedInMode constructor(val type: Int) {
        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_GOOGLE(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_SERVER(3)
    }
}