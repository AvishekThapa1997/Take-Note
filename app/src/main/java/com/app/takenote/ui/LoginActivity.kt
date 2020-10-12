package com.app.takenote.ui

import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.utility.Error
import com.app.takenote.utility.Success
import com.app.takenote.viewmodels.AuthViewModel
import com.app.takenote.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity(), View.OnClickListener{
    override val layoutResourceId: Int
        get() = R.layout.activity_login
    private val authViewModel: AuthViewModel by viewModel<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpButton.setOnClickListener(this)
        proceed.setOnClickListener(this)
        observeUser()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signUpButton -> startIntentFor(SignUpActivity::class.java)
            else -> loginUser()
        }
    }

    private fun loginUser() {
        val email = email.text.toString()
        val password = password.text.toString()
        authViewModel.loginUser(email, password)
    }

    private fun observeUser() {
        authViewModel.currentUser.observe(this) {
            when (it) {
                is Success -> {
                }
                is Error -> showMessage(it.message)
            }
        }
    }
}