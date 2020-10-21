package com.app.takenote.di

import com.app.takenote.repository.AuthRepository
import com.app.takenote.repository.DataRepository
import com.app.takenote.repositoryimpl.AuthRepositoryImpl
import com.app.takenote.repositoryimpl.DataRepositoryImpl
import com.app.takenote.repositoryimpl.ProfileRepositoryImpl
import com.app.takenote.viewmodels.AddProfileViewModel
import com.app.takenote.viewmodels.LoginViewModel
import com.app.takenote.viewmodels.SignUpViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { LoginViewModel(get<AuthRepositoryImpl>()) }
    viewModel { SignUpViewModel(get<AuthRepositoryImpl>()) }
    viewModel { AddProfileViewModel(get<ProfileRepositoryImpl>(), get<DataRepositoryImpl>()) }
}
val firebaseModules = module {
    factory { Firebase.auth }
    factory { Firebase.firestore }
    factory { Firebase.storage.reference }
}

val repositoryModules = module {
    single {
        AuthRepositoryImpl(get(), get<DataRepositoryImpl>())
    }
    single {
        DataRepositoryImpl(get())
    }
    single {
        ProfileRepositoryImpl(get())
    }
}