package com.app.takenote.ui

import android.os.Bundle
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.utility.CustomTypeWriterListener
import com.app.takenote.utility.Success
import com.app.takenote.viewmodels.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {
    companion object {
        const val APP_NAME = "TAKE NOTE"
    }

    private val splashViewModel: SplashViewModel by viewModel()
    private val firebaseAuth: FirebaseAuth by inject()
    override val layoutResourceId: Int = R.layout.activity_splash
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = firebaseAuth.currentUser
        observeExistenceUser()
        animateText()
        textAnimation.setTypeWriterListener(object : CustomTypeWriterListener {
            override fun onTypingEnd(text: String?) {
                if (currentUser != null)
                    splashViewModel.currentUser(currentUser.uid)
                else
                    toLoginActivity()
            }
        })
    }

    private fun observeExistenceUser() {
        splashViewModel.currentUser.observe(this) { response ->
            when (response) {
                is Success -> {
                    val currentUser = response.data
                    if (currentUser.fullName.isEmptyOrIsBlank())
                        startIntentFor(AddProfileActivity::class.java, response.data)
                    else
                        startIntentFor(HomeActivity::class.java, response.data)
                }
                is Error -> toLoginActivity()
            }
            finish()
        }
    }

    private fun animateText() {
        textAnimation.setDelay(2500)
        textAnimation.setWithMusic(false)
        textAnimation.animateText(APP_NAME)
    }

    private fun toLoginActivity() = startIntentFor(LoginActivity::class.java)
}