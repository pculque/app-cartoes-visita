package com.piic.me.data

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val USER_ID = "PICK_ME_PREFERENCES_USER_ID"
    private const val USER_PASSWORD = "PICK_ME_PREFERENCES_PASSWORD"
    private const val SIGNED_IN = "PICK_ME_PREFERENCES_SIGNED_IN"
    private const val PREFERENCES_NAME = "PICK_ME_PREFERENCES"


    fun customPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.userId
        get() = getInt(USER_ID, 0)
        set(value) {
            editMe {
                it.putInt(USER_ID, value)
            }
        }

    var SharedPreferences.readTerm
        get() = getBoolean(SIGNED_IN, false)
        set(value) {
            editMe {
                it.putBoolean(SIGNED_IN, value)
            }
        }
    var SharedPreferences.password
        get() = getString(USER_PASSWORD, "")
        set(value) {
            editMe {
                it.putString(USER_PASSWORD, value)
            }
        }

    var SharedPreferences.clearValues
        get() = run { }
        set(_) {
            editMe {
                it.clear()
            }
        }
}