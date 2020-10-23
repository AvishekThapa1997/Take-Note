package com.app.takenote.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.app.takenote.R
import com.app.takenote.extensions.onBackground
import com.app.takenote.pojo.User
import com.app.takenote.ui.bottomsheet.UpdateNameBottomSheet
import com.app.takenote.utility.BUNDLE
import com.app.takenote.utility.CURRENT_USER
import com.app.takenote.utility.CURRENT_USER_NAME
import com.app.takenote.utility.showImage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.userProfileImage
import kotlinx.android.synthetic.main.add_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ProfileActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_profile
    private var currentUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
//        window.sharedElementEnterTransition = TransitionInflater.from(applicationContext)
//            .inflateTransition(R.transition.shared_transition)
//        window.sharedElementExitTransition = TransitionInflater.from(applicationContext)
//            .inflateTransition(R.transition.shared_transition)
//        window.allowReturnTransitionOverlap = false
//        window.allowEnterTransitionOverlap = false
        super.onCreate(savedInstanceState)
        intent.getBundleExtra(BUNDLE)?.apply {
            if (containsKey(CURRENT_USER))
                currentUser = getParcelable(CURRENT_USER)
        }
        setProfile()
        editUsername.setOnClickListener {
            showUpdateNameBottomSheet()
        }
    }

    private fun showNameInitialLetter() {
        profileNameInitialLetter.text = currentUser?.fullName!![0].toString()
    }

    private fun setProfile() {
        if (!currentUser?.imageUri.isNullOrBlank() && !currentUser?.imageUri.isNullOrEmpty())
            showImage(currentUser?.imageUri!!, userProfileImage, null, {
                showNameInitialLetter()
            })
        else
            showNameInitialLetter()
        fullEmailAddress.text = currentUser?.email
        userProfileName.text = currentUser?.fullName
        onBackground {
            delay(200)
            withContext(Dispatchers.Main) {
                changeProfilePicture.show()
            }
        }
    }

    private fun showUpdateNameBottomSheet() {
        val sheet = UpdateNameBottomSheet()
        sheet.arguments = Bundle().apply {
            putString(CURRENT_USER_NAME, currentUser?.fullName)
        }
        sheet.show(supportFragmentManager, "sheet")
    }
}