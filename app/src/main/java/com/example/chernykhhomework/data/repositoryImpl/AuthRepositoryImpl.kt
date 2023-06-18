package com.example.chernykhhomework.data.repositoryImpl

import android.util.Log
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.AuthorizationApi
import com.example.chernykhhomework.data.network.entity.Auth
import com.example.chernykhhomework.data.network.entity.AuthorizedUser
import com.example.chernykhhomework.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val retrofitService: AuthorizationApi,
    private val sessionData: SessionData
) : AuthRepository {

    override suspend fun login(auth: Auth): String {
        val response = retrofitService.login(auth)
        val token = response.string()
        Log.d("AuthRepositoryImpl", response.string())
        Log.d("AuthRepositoryImpl", token)
        val currentUser = AuthorizedUser(auth.name, auth.password, token)
        sessionData.currentSessionUser = currentUser
        Log.d("AuthRepositoryImpl", sessionData.currentSessionUser.toString())
        return response.string()
    }

    override suspend fun registration(auth: Auth) {
        val response = retrofitService.registration(auth)
        Log.d("AuthRepositoryImpl", response.string())
        login(auth)
    }


}