package com.app.takenote.di


import com.app.takenote.repositoryimpl.AuthRepositoryImpl
import com.app.takenote.repositoryimpl.DataRepositoryImpl
import com.app.takenote.repositoryimpl.ProfileRepositoryImpl
import com.app.takenote.viewmodels.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { LoginViewModel(get<AuthRepositoryImpl>()) }
    viewModel { SignUpViewModel(get<AuthRepositoryImpl>(), get<DataRepositoryImpl>()) }
    viewModel { AddProfileViewModel(get<ProfileRepositoryImpl>(), get<DataRepositoryImpl>()) }
    viewModel { ProfileViewModel(get<ProfileRepositoryImpl>(), get<DataRepositoryImpl>()) }
    viewModel { SplashViewModel(get<DataRepositoryImpl>()) }
    viewModel { NoteUploadViewModel(get<DataRepositoryImpl>()) }
    viewModel { HomeViewModel(get<DataRepositoryImpl>()) }
}
val firebaseModules = module {
    single { Firebase.auth }
    single {
        Firebase.firestore
    }
    single {
        val storage: FirebaseStorage = Firebase.storage
        storage.maxDownloadRetryTimeMillis = 5000
        storage.maxOperationRetryTimeMillis = 5000
        storage.maxUploadRetryTimeMillis = 5000
        storage.reference
    }
}

val repositoryModules = module {
    single {
        AuthRepositoryImpl(get(),get())
    }
    single {
        DataRepositoryImpl(get())
    }
    single {
        ProfileRepositoryImpl(get())
    }
}