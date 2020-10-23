package com.app.takenote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.onBackground
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.AddProfileViewModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class AddProfileActivity : BaseActivity() {
    override val layoutResourceId = R.layout.add_profile
    private val addProfileViewModel: AddProfileViewModel by viewModel()
    private var uid: String? = ""
    private var currentUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeImageUrl()
        observeCurrentUser()
        if (currentUser == null) {
            intent.getBundleExtra(BUNDLE)?.apply {
                currentUser = getParcelable(CURRENT_USER)
                uid = currentUser?.uid
            }
        } else {
            uid = currentUser?.uid
        }
        currentUser?.imageUri?.apply {
            if (!isEmptyOrIsBlank()) {
                addPhotoIcon.hideView(View.GONE)
                showImage(this, userProfileImage)
            }
        }
        addPhotoIcon.setOnClickListener {
            chooseProfilePicture()
        }
        next.setOnClickListener {
            val fullName = fullName.text.toString()
            addProfileViewModel.setName(uid!!, fullName)
            nextProgress.show()
            addPhotoIcon.isEnabled = false
            disabledGroup()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                onBackground {
                    val uri = result.uri
                    val filePath = uri.path
                    withContext(Dispatchers.Main) {
                        addPhotoIcon.visibility = View.GONE
                        uploadProgress.show()
                        addProfileViewModel.uploadPhoto(filePath!!, uid!!)
                    }
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            showMessage(UNABLE_TO_UPLOAD)
    }

    private fun observeImageUrl() {
        addProfileViewModel.imageUrl.observe(this) { response ->
            when (response) {
                is Success -> {
                    showImage(response.data, userProfileImage, { successMessage ->
                        showMessage(successMessage)
                        uploadProgress.hide()
                    }, { errorMessage ->
                        showMessage(errorMessage)
                        uploadProgress.hide()
                    })
                }
                is Error -> {
                    showMessage(response.message)
                    uploadProgress.hide()
                }
            }
        }
    }

    private fun observeCurrentUser() {
        addProfileViewModel.currentUser.observe(this) { response ->
            when (response) {
                is Success -> {
                    startIntentFor(HomeActivity::class.java, response.data)
                    finishAffinity()
                }
                is Error -> showMessage(response.message)
            }
            nextProgress.hide()
            enabledGroup()
            addPhotoIcon.isEnabled = true
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
}