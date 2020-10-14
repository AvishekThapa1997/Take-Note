package com.app.takenote.utility

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.tozny.crypto.android.AesCbcWithIntegrity

fun View.showMessage(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun encodeString(data: String): String {
    val salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt())
    val keys: AesCbcWithIntegrity.SecretKeys =
        AesCbcWithIntegrity.generateKeyFromPassword(data, salt)
    val cipherTextIvMac = AesCbcWithIntegrity.encrypt(data, keys)
    val cipherTextString = cipherTextIvMac.toString()
    return cipherTextString
}