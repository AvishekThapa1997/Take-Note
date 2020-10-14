package com.app.takenote.di

import com.app.takenote.viewmodels.LoginViewModel
import com.app.takenote.viewmodels.SignUpViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { LoginViewModel() }
    viewModel { SignUpViewModel() }
}
val firebaseModules = module {
    factory { Firebase.auth }
    factory { Firebase.firestore }
}