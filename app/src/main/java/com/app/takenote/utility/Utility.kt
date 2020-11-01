package com.app.takenote.utility

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.tozny.crypto.android.AesCbcWithIntegrity

fun View.showMessage(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

//fun encodeString(data: String): String {
//    val salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt())
//    val keys: AesCbcWithIntegrity.SecretKeys =
//        AesCbcWithIntegrity.generateKeyFromPassword(data, salt)
//    val cipherTextIvMac = AesCbcWithIntegrity.encrypt(data, keys)
//    return cipherTextIvMac.toString()
//}

fun Context.showImage(
    imageUrl: String,
    targetView: ImageView,
    onSuccess: ((message: String) -> Unit?)? = null,
    onError: ((message: String) -> Unit?)? = null
) {
    Glide.with(this).load(imageUrl).apply {
        RequestOptions().dontTransform()
    }.listener(object : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onSuccess?.invoke(PHOTO_UPLOADED_SUCCESSFULLY)
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onError?.invoke(UNABLE_TO_UPLOAD)
            return false
        }
    }).into(targetView)
}

fun Activity.hideKeyboard(token: IBinder? = null) {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    token?.let {
        inputMethodManager.hideSoftInputFromWindow(
            token,
            0
        )
    }
}

//fun Activity.openKeyboard() {
//    val inputMethodManager: InputMethodManager =
//        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.toggleSoftInput(InputMethodManager.RESULT_SHOWN,0)
//    }




