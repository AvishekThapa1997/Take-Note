package com.app.takenote.ui


import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.AuthViewModel
import com.app.takenote.viewmodels.SignUpViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_signup.*
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_signup
    private val authViewModel: AuthViewModel by viewModel<SignUpViewModel>()
    private var listener: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeCurrentUser()
        register.setOnClickListener {
            signUpUser()
        }
    }


    private fun observeCurrentUser() {
        authViewModel.currentUserId.observe(this) { response ->
            when (response) {
                is Success -> {
                    if (listener == null) {
                        observeRealTimeUpdates(response.data)
                    }
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
        disabledGroup()
        signUpProgress.showView()
        authViewModel.signUpUser(email, password, confirmPassword)
    }

    private fun enabledGroup() {
        signUpGroup.referencedIds.forEach { viewId ->
            findViewById<View>(viewId).isEnabled = true
        }
    }

    private fun disabledGroup() {
        signUpGroup.referencedIds.forEach { viewId ->
            findViewById<View>(viewId).isEnabled = false
        }
    }

    private fun observeRealTimeUpdates(userId: String?) {
        listener = fireStore.collection(COLLECTION_NAME).document(userId!!)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val currentUser = User(
                        documentSnapshot[EMAIL].toString(),
                        documentSnapshot[PRIMARY_ID].toString(),
                        documentSnapshot[IMAGE_URL].toString()
                    )
                    startIntentFor(
                        AddProfileActivity::class.java,
                        currentUser
                    )
                    finishAffinity()
                } else {
                    if (error != null) {
                        showMessage(SOMETHING_WENT_WRONG)
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.remove()
    }
}