package com.app.takenote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.setImageUrlUploadWorker
import com.app.takenote.extensions.setUpImageUploadWorker
import com.app.takenote.extensions.setWork
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.AddProfileViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_profile.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class AddProfileActivity : BaseActivity() {
    override val layoutResourceId = R.layout.add_profile
    private val addProfileViewModel: AddProfileViewModel by viewModel()
    private var currentUser: User? = null
    private var realTimeListener: ListenerRegistration? = null
    private lateinit var group: Group
    private lateinit var workManager: WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(this)
        intent.getBundleExtra(BUNDLE)?.apply {
            currentUser = getParcelable(CURRENT_USER)
        }
        group = findViewById(R.id.group)
        currentUser?.apply {
            if (!imageUri?.isEmptyOrIsBlank()!!) {
                addPhotoIcon.hideView(View.GONE)
                showImage(imageUri, userProfileImage)
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
            addProfileViewModel.setName(currentUser?.uid!!, fullName)
        }
        observeError()
    }

    private fun observeError() {
        addProfileViewModel.errorMessage.observe(this) { error ->
            when (error) {
                is ImageUploadError -> {
                    showMessage(error.message)
                    hideImageUploadProgress()
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
        realTimeListener = fireStore.collection(COLLECTION_NAME).document(currentUser?.uid!!)
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val updatedFullName =
                        documentSnapshot[FULL_NAME].toString()
                    val updatedImageUri =
                        documentSnapshot[IMAGE_URL].toString()
                    if (currentUser?.imageUri != updatedImageUri) {
                        currentUser = currentUser?.copy(imageUri = updatedImageUri)
                        showImage(updatedImageUri, userProfileImage, { successMessage ->
                            uploadProgress.hide()
                            showMessage(successMessage)
                        }, { errorMessage ->
                            showMessage(errorMessage)
                        })
                    }
                    if (currentUser?.fullName != updatedFullName) {
                        currentUser = currentUser?.copy(fullName = updatedFullName)
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
                val uri = result.uri
                val filePath = uri.path
                showImageUploadProgress()
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
                    hideImageUploadProgress()
                }
            }
        }
    }

    private fun showImageUploadProgress() {
        addPhotoIcon.hideView(View.GONE)
        uploadProgress.show()
    }

    private fun hideImageUploadProgress() {
        addPhotoIcon.showView()
        uploadProgress.hide()
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