package com.app.takenote.ui


import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.utility.Error
import com.app.takenote.utility.Success
import com.app.takenote.viewmodels.AuthViewModel
import com.app.takenote.viewmodels.SignUpViewModel
import kotlinx.android.synthetic.main.activity_signup.*
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_signup
    private val authViewModel: AuthViewModel by viewModel<SignUpViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeCurrentUser()
        register.setOnClickListener {
            signUpUser()
        }
    }



    private fun observeCurrentUser() {
        authViewModel.currentUser.observe(this) { response ->
            when (response) {
                is Success -> {
                    startIntentFor(AddProfileActivity::class.java, response.data)
                    finishAffinity()
                }
                is Error -> {
                    showMessage(response.message)
                    signUpProgress.hideView(View.GONE)
                    enabledGroup()
                }
            }
        }
    }

    private fun signUpUser() {
        val email = userEmail.text.toString().trim()
        val password = userPassword.text.toString().trim()
        val confirmPassword = confirmPassword.text.toString().trim()
        authViewModel.signUpUser(email, password, confirmPassword)
        disabledGroup()
        signUpProgress.showView()
    }

    private fun enabledGroup(){
        signUpGroup.referencedIds.forEach {viewId ->
            findViewById<View>(viewId).isEnabled = true
        }
    }

    private fun disabledGroup() {
        signUpGroup.referencedIds.forEach {viewId ->
            findViewById<View>(viewId).isEnabled = false
        }
    }
}