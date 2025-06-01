package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: LiveData<Boolean> = _loading.asLiveData()

    private val _error = MutableStateFlow(false)
    val error: LiveData<Boolean> = _error.asLiveData()

    private val container = DependencyContainer.getInstance()

    val success: LiveData<Boolean> = container.appAuth.authState.map { it != null }
        .asLiveData()

    fun login(login: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val (id, token) = container.apiService.updateUser(login, password)
                container.appAuth.setAuth(id, token)
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