package com.app.takenote.ui

import android.os.Bundle
import com.app.takenote.R
import kotlinx.android.synthetic.main.activity_auth_ui.*

class AuthUIActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_auth_ui
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInWithEmailPassword.setOnClickListener {
            startIntentFor(LoginActivity::class.java)
            finishAffinity()
        }
    }
}