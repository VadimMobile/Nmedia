package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState

class AuthViewModel(
    private val appAuth: AppAuth,
) : ViewModel() {
    val state: LiveData<AuthState?> = appAuth
        .authState.asLiveData()

    val isAuthenticated: Boolean
        get() = (appAuth.authState.value?.userId ?: 0L) != 0L
}