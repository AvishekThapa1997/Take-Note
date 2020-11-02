package com.app.takenote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.Group
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.onBackground
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.AddProfileViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class AddProfileActivity : BaseActivity() {
    override val layoutResourceId = R.layout.add_profile
    private val addProfileViewModel: AddProfileViewModel by viewModel()
    private var currentUser: User? = null
    private var realTimeListener: ListenerRegistration? = null
    private lateinit var group: Group
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getBundleExtra(BUNDLE)?.apply {
            currentUser = getParcelable(CURRENT_USER)
        }
        group = findViewById(R.id.group)
        currentUser?.apply {
            if (!imageUri?.isEmptyOrIsBlank()!!) {
                addPhotoIcon.hideView(View.GONE)
                showImage(imageUri!!, userProfileImage)
            } else {
                addPhotoIcon.showView()
            }
        }
        addPhotoIcon.setOnClickListener {
            chooseProfilePicture()
        }
        next.setOnClickListener {
            val fullName = fullName.text.toString()
            nextProgress.showView()
            addPhotoIcon.isEnabled = false
            disabledGroup()
            addProfileViewModel.setName(currentUser?.uid!!, fullName) { errorMessage ->
                showMessage(errorMessage)
                nextProgress.hideView(View.GONE)
                addPhotoIcon.isEnabled = true
                enabledGroup()
            }
        }
        observeError()
    }

    private fun observeError() {
        addProfileViewModel.errorMessage.observe(this) { error ->
            when (error) {
                is ImageUploadError -> {
                    showMessage(error.message)
                    uploadProgress.hide()
                    addPhotoIcon.showView()
                }
                is NameUpdateError -> {
                    showMessage(error.message)
                    nextProgress.hideView(View.GONE)
                    addPhotoIcon.isEnabled = true
                    enabledGroup()
                }
            }

        }
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
                        showImage(updatedImageUri, userProfileImage, { successMessage ->
                            uploadProgress.hide()
                            showMessage(successMessage)
                        }, { errorMessage ->
                            showMessage(errorMessage)
                        })
                    }
                    if (currentUser?.fullName != updatedFullName) {
                        currentUser?.fullName = updatedFullName
                        startIntentFor(HomeActivity::class.java, currentUser)
                        finishAffinity()
                    }
                } else {
                    if (error != null) {
                        showMessage(SOMETHING_WENT_WRONG)
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        realTimeListener?.remove()
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
                        addProfileViewModel.uploadPhoto(
                            filePath!!,
                            currentUser?.uid!!
                        )
                    }
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            showMessage(UNABLE_TO_UPLOAD)
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