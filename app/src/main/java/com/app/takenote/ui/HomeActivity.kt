package com.app.takenote.ui


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), View.OnClickListener {

    override val layoutResourceId: Int = R.layout.activity_home
    private var currentUser: User? = null
    private lateinit var realTimeListener: ListenerRegistration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchNote.setOnFocusChangeListener { _, hasFocus ->
            searchNote.isCursorVisible = hasFocus
        }
        val intent = intent
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        currentUser?.let {
            if (!it.imageUri.isEmptyOrIsBlank()) {
                startShimmer()
                showProfileImage(it)
            } else {
                if (!it.fullName.isEmptyOrIsBlank()) {
                    showProfileName(it)
                    stopShimmer()
                }
            }
        }
        profileImage.setOnClickListener(this)
        profile.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        realTimeListener = fireStore.collection(COLLECTION_NAME).document(currentUser?.uid!!)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, _ ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val updatedFullName =
                        documentSnapshot[FULL_NAME].toString()
                    val updatedImageUri =
                        documentSnapshot[IMAGE_URL].toString()
                    if (currentUser?.imageUri != updatedImageUri) {
                        currentUser = currentUser?.copy(imageUri = updatedImageUri)
                        startShimmer()
                        showProfileImage(currentUser!!)
                    }
                    if (currentUser?.fullName != updatedFullName) {
                        currentUser = currentUser?.copy(fullName = updatedFullName)
                        if (currentUser?.imageUri.isEmptyOrIsBlank())
                            showProfileName(currentUser!!)
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        realTimeListener.remove()
    }

    private fun showProfileImage(currentUser: User) {
        if (profile.isVisible)
            profile.hideView(View.INVISIBLE)
        showImage(currentUser.imageUri!!, profileImage, {
            if (shimmerLayout.isShimmerVisible)
                stopShimmer()
        }, {
            if (shimmerLayout.isShimmerVisible)
                stopShimmer()
            showProfileName(currentUser)
        })
        profileImage.showView()
    }

    private fun startShimmer() {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
    }

    private fun showProfileName(currentUser: User) {
        profile.text = currentUser.fullName!![0].toString()
        profile.showView()
    }

    override fun enabledFullScreen(): Boolean = false
    override fun onClick(v: View?) =
        startIntentFor(ProfileActivity::class.java, currentUser)

}
