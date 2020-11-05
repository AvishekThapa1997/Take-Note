package com.app.takenote.ui


import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.AuthViewModel
import com.app.takenote.viewmodels.LoginViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity(), View.OnClickListener {
    override val layoutResourceId: Int
        get() = R.layout.activity_login
    private val authViewModel: AuthViewModel by viewModel<LoginViewModel>()
    private var listener: ListenerRegistration? = null
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
        loginProgress.showView()
        disabledGroup()
        authViewModel.loginUser(email, password)
    }

    private fun observeUser() {
        authViewModel.currentUserId.observe(this) { response ->
            when (response) {
                is Success -> {
                    if (listener == null) {
                        observeRealTimeUpdates(response.data)
                    }
                }
                is Error -> {
                    showMessage(response.message)
                    loginProgress.hideView(View.GONE)
                    enabledGroup()
                }
            }
        }
    }

    private fun enabledGroup() {
        isEnabledGroup(true)
    }

    private fun disabledGroup() {
        isEnabledGroup(false)
    }

    private fun isEnabledGroup(enabled: Boolean) {
        group.referencedIds.forEach { viewId ->
            findViewById<View>(viewId).isEnabled = enabled
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.remove()
    }

    private fun observeRealTimeUpdates(userId: String?) {
        observing = true
        listener = fireStore.collection(COLLECTION_NAME).document(userId!!)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val currentUser = User(
                        documentSnapshot[FULL_NAME].toString(),
                        documentSnapshot[EMAIL].toString(),
                        documentSnapshot[IMAGE_URL].toString(),
                        documentSnapshot[PRIMARY_ID].toString(),

                        )
                    if (currentUser.fullName.isEmptyOrIsBlank()) {
                        startIntentFor(
                            AddProfileActivity::class.java,
                            currentUser
                        )
                    } else {
                        startIntentFor(
                            HomeActivity::class.java,
                            currentUser
                        )
                    }
                    finishAffinity()
                } else {
                    if (error != null) {
                        showMessage(SOMETHING_WENT_WRONG)
                    }
                }
            }
    }
}