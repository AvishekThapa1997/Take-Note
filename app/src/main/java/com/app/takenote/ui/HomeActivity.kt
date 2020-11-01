package com.app.takenote.ui


import android.os.Bundle
import android.util.Log
import android.view.View
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.add_profile.*

class HomeActivity : BaseActivity(), View.OnClickListener {
//    companion object {
//        const val PROFILE_IMAGE_TRANSITION = "profileImage"
//        const val PROFILE_NAME_TRANSITION = "profileName"
//    }

    override val layoutResourceId: Int = R.layout.activity_home
    private var currentUser: User? = null
    private lateinit var realTimeListener: ListenerRegistration
    override fun onCreate(savedInstanceState: Bundle?) {
//        window.sharedElementExitTransition = TransitionInflater.from(applicationContext)
//            .inflateTransition(R.transition.shared_transition)
//        window.sharedElementReturnTransition = TransitionInflater.from(applicationContext)
//            .inflateTransition(R.transition.shared_transition)
//        window.sharedElementReenterTransition = false
//        window.allowEnterTransitionOverlap = false
//        window.allowReturnTransitionOverlap = false
//        window.allowEnterTransitionOverlap = false
        super.onCreate(savedInstanceState)
        searchNote.setOnFocusChangeListener { _, hasFocus ->
            searchNote.isCursorVisible = hasFocus
        }
        val intent = intent
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        Log.i("TAG", "onCreate: $currentUser")
        currentUser?.let {
            if (!it.imageUri.isEmptyOrIsBlank()) {
                showProfileImage(it)
            } else {
                if (!it.fullName.isEmptyOrIsBlank()) {
                    showProfileName(it)
                }
            }
        }

        profileImage.setOnClickListener(this)
        profile.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        realTimeListener = firestore.collection(COLLECTION_NAME).document(currentUser?.uid!!)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val updatedFullName =
                        documentSnapshot[FULL_NAME].toString()
                    val updatedImageUri =
                        documentSnapshot[IMAGE_URL].toString()
                    if (currentUser?.imageUri != updatedImageUri) {
                        currentUser?.imageUri = updatedImageUri
                        showImage(updatedImageUri, userProfileImage)
                    }
                    if (currentUser?.fullName != updatedFullName) {
                        currentUser?.fullName = updatedFullName
                        if (currentUser?.imageUri.isEmptyOrIsBlank())
                            showProfileName(currentUser!!)
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        realTimeListener.remove()
    }

    private fun showProfileImage(currentUser: User) {
        showImage(currentUser.imageUri!!, profileImage)
        profileImage.showView()
    }

    private fun showProfileName(currentUser: User) {
        profile.text = currentUser.fullName!![0].toString()
        profile.showView()
    }

    override fun enabledFullScreen(): Boolean = false
    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.userProfileImage -> {
//                val options = createSceneTransition(profileImage, PROFILE_IMAGE_TRANSITION)
//                startIntentFor(ProfileActivity::class.java, currentUser, options.toBundle())
//                //startIntentFor(ProfileActivity::class.java,currentUser)
//            }
//            else -> {
//                val options = createSceneTransition(profile, PROFILE_NAME_TRANSITION)
//                startIntentFor(ProfileActivity::class.java, currentUser, options.toBundle())
//            }
//        }
        //startIntentFor(ProfileActivity::class.java, currentUserId)
    }

}
