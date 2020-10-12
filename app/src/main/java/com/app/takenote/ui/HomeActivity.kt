package com.app.takenote.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.takenote.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {
    override val layoutResourceId: Int = R.layout.activity_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchNote.setOnFocusChangeListener { _, hasFocus ->
            searchNote.isCursorVisible = hasFocus
        }
    }

    override fun enabledFullScreen(): Boolean = false
}
