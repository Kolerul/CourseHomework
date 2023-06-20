package com.example.chernykhhomework.data.repositoryImpl

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.AuthorizationApi
import com.example.chernykhhomework.data.network.entity.Auth
import com.example.chernykhhomework.data.network.entity.AuthorizedUser
import com.example.chernykhhomework.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val retrofitService: AuthorizationApi,
    private val sessionData: SessionData,
    private val context: Context
) : AuthRepository {

    override suspend fun login(auth: Auth): Auth {
        val response = retrofitService.login(auth)
        val token = response.string()
        Log.d("AuthRepositoryImpl", response.string())
        Log.d("AuthRepositoryImpl", token)
        val currentUser = AuthorizedUser(auth.name, auth.password, token)
        sessionData.currentSessionUser = currentUser
        addToSharedPref(auth)
        Log.d("AuthRepositoryImpl", sessionData.currentSessionUser.toString())
        return auth
    }

    override suspend fun registration(auth: Auth): Auth {
        val response = retrofitService.registration(auth)
        Log.d("AuthRepositoryImpl", response.string())
        return login(auth)
    }

    override suspend fun autoLogin(): Auth? {
        val sharedPreferences = createEncryptedSharedPref()
        val login = sharedPreferences.getString(LOGIN_KEY, "")
        val password = sharedPreferences.getString(PASSWORD_KEY, "")
        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            return null
        }
        val auth = Auth(login, password)
        return login(auth)
    }

    private fun createEncryptedSharedPref(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_USER_DATA,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun addToSharedPref(auth: Auth) {
        val sharedPreferences = createEncryptedSharedPref()
        sharedPreferences.edit()
            .putString(LOGIN_KEY, auth.name)
            .putString(PASSWORD_KEY, auth.password)
            .apply()
    }


    companion object {
        const val ENCRYPTED_USER_DATA = "encrypted_user"
        const val LOGIN_KEY = "login"
        const val PASSWORD_KEY = "password"
    }
}