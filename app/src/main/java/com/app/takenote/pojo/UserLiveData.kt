package com.app.takenote.pojo

import androidx.lifecycle.LiveData

class UserLiveData : LiveData<User>() {
    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }
}