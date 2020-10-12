package com.app.takenote.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.app.takenote.utility.showMessage

abstract class BaseActivity : AppCompatActivity() {
    open val layoutResourceId = 0
    open fun enabledFullScreen(): Boolean = true
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setContentView(layoutResourceId)
    }

    fun <T> startIntentFor(activityClass: Class<T>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
    protected fun showMessage(message : String) = findViewById<View>(android.R.id.content).showMessage(message)
}