package com.app.takenote.ui


import android.os.Bundle
import android.view.View
import com.app.takenote.R
import com.app.takenote.pojo.User
import com.app.takenote.utility.BUNDLE
import com.app.takenote.utility.CURRENT_USER
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchNote.setOnFocusChangeListener { _, hasFocus ->
            searchNote.isCursorVisible = hasFocus
        }
        val intent = intent
        val bundle = intent.getBundleExtra(BUNDLE)
        val currentUser = bundle?.getParcelable<User>(CURRENT_USER)
        currentUser?.let {
            if (it.imageUri.isNullOrEmpty() && it.imageUri.isNullOrBlank()) {
                profile.text = it.fullName?.get(0).toString()
                profile.visibility = View.VISIBLE
            }
        }
    }

    override fun enabledFullScreen(): Boolean = false
}
