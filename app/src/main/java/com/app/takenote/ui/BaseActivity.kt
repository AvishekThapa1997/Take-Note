package com.app.takenote.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.google.firebase.firestore.FirebaseFirestore
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.koin.android.ext.android.get

abstract class BaseActivity : AppCompatActivity() {
    open val layoutResourceId = 0
    open fun enabledFullScreen(): Boolean = true
    protected val fireStore: FirebaseFirestore = get()
    protected var observing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (enabledFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.apply {
                setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setContentView(layoutResourceId)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    protected open fun <T> startIntentFor(
        activityClass: Class<T>,
        currentUser: User? = null,
        transitionBundle: Bundle? = null
    ) {
        val intent = Intent(this, activityClass)
        currentUser?.let {
            val bundle = Bundle()
            bundle.putParcelable(CURRENT_USER, currentUser)
            intent.putExtra(BUNDLE, bundle)
        }
        transitionBundle?.let {
            startActivity(intent, it)
        } ?: run {
            startActivity(intent)
        }

    }

    protected fun showMessage(message: String) =
        findViewById<View>(android.R.id.content).showMessage(message)

    protected fun chooseProfilePicture() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    protected fun View.showView() {
        visibility = View.VISIBLE
    }

    protected fun View.hideView(status: Int) {
        visibility = status
    }
}