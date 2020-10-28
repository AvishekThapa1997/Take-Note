package com.app.takenote.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class TakeNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TakeNoteApplication)
            loadKoinModules(arrayListOf(firebaseModules,repositoryModules,viewModelModules,
                networkModules))
        }
    }
}