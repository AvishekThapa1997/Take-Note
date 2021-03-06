package com.app.takenote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.work.WorkManager
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.setImageUrlUploadWorker
import com.app.takenote.extensions.setUpImageUploadWorker
import com.app.takenote.extensions.setWork
import com.app.takenote.pojo.User
import com.app.takenote.ui.bottomsheet.UpdateNameBottomSheet
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.ProfileViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.userProfileImage
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class ProfileActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_profile
    private var currentUser: User? = null
    private val profileViewModel: ProfileViewModel by viewModel()
    private var sheetDialog: UpdateNameBottomSheet? = null
    private lateinit var workManager: WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(this)
        intent.getBundleExtra(BUNDLE)?.apply {
            currentUser = getParcelable(CURRENT_USER)
        }
        setProfile()
        editUsername.setOnClickListener {
            showUpdateNameBottomSheet()
        }
        changeProfilePicture.setOnClickListener {
            chooseProfilePicture()
        }
        btnLogout.setOnClickListener {
            logoutUser()
        }
        currentUser?.let {
            observeRealUpdates(it.uid!!)
        }
        observeError()
        observeToLogout()
    }

    private fun observeToLogout() {
        profileViewModel.logout.observe(this) {
            startIntentFor(LoginActivity::class.java)
            finishAffinity()
        }
    }

    private fun observeError() {
        profileViewModel.errorMessage.observe(this) { error ->
            when (error) {
                is ImageUploadError -> {
                    stopShimmer()
                    showMessage(error.message)
                    if (!currentUser?.imageUri.isEmptyOrIsBlank())
                        userProfileImage.showView()
                    else
                        profileNameInitialLetter.showView()
                    changeProfilePicture.show()
                }
                is NameUpdateError -> {
                    showMessage(error.message)
                }
            }
        }
    }

    private fun observeRealUpdates(userId: String) {
        fireStore.collection(COLLECTION_NAME).document(userId)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, fireStoreException: FirebaseFirestoreException? ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val updatedName = documentSnapshot[FULL_NAME].toString()
                    val updatedImageUri = documentSnapshot[IMAGE_URL].toString()
                    if (currentUser?.imageUri != updatedImageUri) {
                        currentUser = currentUser?.copy(imageUri = updatedImageUri)
                        applicationContext.showImage(
                            updatedImageUri,
                            userProfileImage,
                            { successMessage ->
                                if (shimmerLayout.isShimmerVisible)
                                    stopShimmer()
                                showMessage(successMessage)
                                userProfileImage.showView()
                                profileNameInitialLetter.hideView(View.INVISIBLE)
                                changeProfilePicture.show()
                            },
                            {
                                showNameInitialLetter()
                            })
                    }
                    if (updatedName != currentUser?.fullName) {
                        currentUser = currentUser?.copy(fullName = updatedName)
                        userProfileName.text = currentUser?.fullName
                        if (currentUser?.imageUri.isEmptyOrIsBlank())
                            showNameInitialLetter()
                        closeDialog()
                    }
                } else {
                    if (fireStoreException != null)
                        showMessage(SOMETHING_WENT_WRONG)
                }
            }
    }

    private fun closeDialog() {
        sheetDialog?.dismiss()
    }

    private fun showNameInitialLetter() {
        profileNameInitialLetter.text = currentUser?.fullName!![0].toString()
        profileNameInitialLetter.showView()
    }

    private fun setProfile() {
        if (!currentUser?.imageUri.isEmptyOrIsBlank()) {
            startShimmer()
            applicationContext.showImage(currentUser?.imageUri!!, userProfileImage, {
                stopShimmer()
            }, {
                stopShimmer()
                showNameInitialLetter()
            })
        } else
            showNameInitialLetter()
        fullEmailAddress.text = currentUser?.email
        userProfileName.text = currentUser?.fullName
        changeProfilePicture.show()
    }

    private fun showUpdateNameBottomSheet() {
        sheetDialog = UpdateNameBottomSheet()
        sheetDialog?.arguments = Bundle().apply {
            putString(CURRENT_USER_NAME, currentUser?.fullName)
            putString(PRIMARY_ID, currentUser?.uid)
        }
        sheetDialog?.show(supportFragmentManager, "sheet")
    }

    private fun startShimmer() = shimmerLayout.showView()

    private fun stopShimmer() = shimmerLayout.hideView(View.GONE)

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
                currentUser?.uid?.let { userId ->
                    val imageUploadWorker = workManager.setWork(userId, filePath!!)
                    observeResultFromImageUploadWorker(imageUploadWorker.id)
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            showMessage(UNABLE_TO_UPLOAD)
    }

    private fun observeResultFromImageUploadWorker(id: UUID) {
        workManager.getWorkInfoByIdLiveData(id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                val result = workInfo.outputData.getString(WORKER_RESULT)
                if (result != null && result == SOMETHING_WENT_WRONG) {
                    showMessage(result)
                    stopShimmer()
                    currentUser?.imageUri?.let { imageUrl ->
                        if (!imageUrl.isEmptyOrIsBlank()) {
                            userProfileImage.showView()
                        } else
                            profileNameInitialLetter.showView()
                    }
                    changeProfilePicture.show()
                }
            }
        }
    }

    private fun logoutUser() {
        profileViewModel.logoutUser()
    }
}

