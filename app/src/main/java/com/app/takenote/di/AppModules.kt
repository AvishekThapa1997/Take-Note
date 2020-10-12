package com.app.takenote.di

import com.app.takenote.viewmodels.LoginViewModel
import com.app.takenote.viewmodels.SignUpViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { LoginViewModel() }
    viewModel { SignUpViewModel() }
}