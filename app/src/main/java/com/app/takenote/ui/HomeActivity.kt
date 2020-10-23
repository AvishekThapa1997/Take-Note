package com.app.takenote.ui


import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.pojo.User
import com.app.takenote.utility.BUNDLE
import com.app.takenote.utility.CURRENT_USER
import com.app.takenote.utility.showImage
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), View.OnClickListener {
    companion object {
        const val PROFILE_IMAGE_TRANSITION = "profileImage"
        const val PROFILE_NAME_TRANSITION = "profileName"
    }

    override val layoutResourceId: Int = R.layout.activity_home
    private var currentUser: User? = null
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
        currentUser = bundle?.getParcelable<User>(CURRENT_USER)
        currentUser?.let {
            if (!it.imageUri.isNullOrEmpty() && !it.imageUri.isNullOrBlank()) {
                showImage(it.imageUri, profileImage)
                profileImage.showView()
            } else {
                profile.text = it.fullName?.get(0).toString()
                profile.showView()
            }
        }

        profileImage.setOnClickListener(this)
        profile.setOnClickListener(this)
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
        startIntentFor(ProfileActivity::class.java, currentUser)
    }

//    private fun createSceneTransition(view: View, transitionName: String): ActivityOptions {
//        return ActivityOptions.makeSceneTransitionAnimation(
//            this,
//            view,
//            transitionName
//        )
//    }
}
