package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth

class LoginViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: LiveData<Boolean> = _loading.asLiveData()

    private val _error = MutableStateFlow(false)
    val error: LiveData<Boolean> = _error.asLiveData()

    val success: LiveData<Boolean> = AppAuth.getInstance().authState.map { it != null }
        .asLiveData()

    fun login(login: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val (id, token) = PostsApi.service.updateUser(login, password)
                AppAuth.getInstance().setAuth(id, token)
            } catch (_: Exception) {
                _error.value = true
            } finally {
                _loading.value = false
            }
        }
    }

    fun onErrorShown() {
        _error.value = false
    }
}