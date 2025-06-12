package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val ID_KEY = "ID_KEY"
    }

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
    fun removeAuth() {
        _authState.value = null
        prefs.edit() { clear() }
    }

}