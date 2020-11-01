package com.app.takenote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.pojo.User
import com.app.takenote.ui.bottomsheet.UpdateNameBottomSheet
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.ProfileViewModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.userProfileImage
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_profile
    private var currentUser: User? = null
    private val profileViewModel: ProfileViewModel by viewModel()
    private var sheetDialog: UpdateNameBottomSheet? = null
    override fun onCreate(savedInstanceState: Bundle?) {
//        window.sharedElementEnterTransition = TransitionInflater.from(applicationContext)
//            .inflateTransition(R.transition.shared_transition)
//        window.sharedElementExitTransition = TransitionInflater.from(applicationContext)
//            .inflateTransition(R.transition.shared_transition)
//        window.allowReturnTransitionOverlap = false
//        window.allowEnterTransitionOverlap = false
        super.onCreate(savedInstanceState)
//        intent.getBundleExtra(BUNDLE)?.apply {
//            if (containsKey(CURRENT_USER))
//                currentUser = getParcelable(CURRENT_USER)
//        }
//        setProfile()
//        editUsername.setOnClickListener {
//            showUpdateNameBottomSheet()
//        }
//        changeProfilePicture.setOnClickListener {
//            chooseProfilePicture()
//        }
//        observeUpdatedUser()
    }

    private fun showNameInitialLetter() {
        profileNameInitialLetter.text = currentUser?.fullName!![0].toString()
        profileNameInitialLetter.showView()
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
        changeProfilePicture.show()

    }


    private fun showUpdateNameBottomSheet() {
        if (sheetDialog == null)
            sheetDialog = UpdateNameBottomSheet()
        sheetDialog?.arguments = Bundle().apply {
            putString(CURRENT_USER_NAME, currentUser?.fullName)
            putString(PRIMARY_ID, currentUser?.uid)
        }
        sheetDialog?.show(supportFragmentManager, "sheet")
    }

    private fun observeUpdatedUser() {
        profileViewModel.currentUser.observe(this) { response ->
            when (response) {
                is Success -> {
                    if (currentUser?.fullName != response.data.fullName) {
                        userProfileName.text = response.data.fullName
                        sheetDialog?.dismiss()
                        showMessage(SUCCESSFULLY_NAME_UPDATED)
                    } else
                        showImage(response.data.imageUri!!, userProfileImage, {
                            stopShimmer()
                            profileNameInitialLetter.hideView(View.INVISIBLE)
                            changeProfilePicture.show()
                        }, {
                            showHiddenViews()
                        })
                    currentUser = response.data
                }
                is Error -> {
                    showMessage(response.message)
                    stopShimmer()
                    showHiddenViews()
                }
            }
            userProfileImage.showView()
        }
    }

    private fun startShimmer() = shimmerLayout.showView()

    private fun stopShimmer() = shimmerLayout.hideView(View.GONE)

    private fun showHiddenViews() {
        changeProfilePicture.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val uri = result.uri
                val filePath = uri.path
                startShimmer()
                userProfileImage.hideView(View.INVISIBLE)
                profileNameInitialLetter.hideView(View.INVISIBLE)
                changeProfilePicture.hide()
                profileViewModel.updateProfilePhoto(currentUser?.uid!!, filePath!!)
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            showMessage(UNABLE_TO_UPLOAD)
    }
}

