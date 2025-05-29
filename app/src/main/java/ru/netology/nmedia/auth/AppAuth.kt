package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppAuth(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow<AuthState?>(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    init {
        val id = prefs.getLong(ID_KEY, 0L)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id == 0L || token == null) {
            prefs.edit {
                clear()
            }
        } else {
            _authState.value = AuthState(userId = id, token = token)
        }
    }

    @Synchronized
    fun setAuth(userId: Long, token: String) {
        _authState.value = AuthState(userId, token)
        prefs.edit() {
            putString(TOKEN_KEY, token)
            putLong(ID_KEY, userId)
        }
    }

    @Synchronized
    fun removeAuth(){
        _authState.value = null
        prefs.edit() {clear()}
    }

}

data class AuthState(val id: Long = 0, val token: String? = null)