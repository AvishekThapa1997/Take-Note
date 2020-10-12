package com.app.takenote.ui


import android.os.Bundle
import com.app.takenote.R
import com.app.takenote.ui.BaseActivity
import com.facebook.CallbackManager



class MainActivity : BaseActivity() {
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        callbackManager = CallbackManager.Factory.create()
//        facebookLogin.setPermissions(mutableListOf("email"))
//        facebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onError(error: FacebookException?) {
//                Log.i("TAG", "onError: ${error?.localizedMessage}")
//            }
//
//            override fun onSuccess(result: LoginResult?) {
//               Toast.makeText(applicationContext,"Success",Toast.LENGTH_LONG).show()
//            }
//
//            override fun onCancel() {
//                Toast.makeText(applicationContext,"Cancel",Toast.LENGTH_LONG).show()
//            }
//        })
    }
}