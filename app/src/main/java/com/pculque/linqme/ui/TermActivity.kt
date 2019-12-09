package com.pculque.linqme.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pculque.linqme.R
import com.pculque.linqme.data.PreferenceHelper
import com.pculque.linqme.data.PreferenceHelper.readTerm
import kotlinx.android.synthetic.main.activity_term.*
import android.content.Intent


class TermActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = PreferenceHelper.customPreference(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term)
        btnCancel.setOnClickListener {
            prefs.readTerm = false
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }
        btnAccept.setOnClickListener {
            prefs.readTerm = true
            finish()
        }
    }
}
