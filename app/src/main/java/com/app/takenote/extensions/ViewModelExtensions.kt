package com.app.takenote.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun ViewModel.runIO(body: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {
    body()
}