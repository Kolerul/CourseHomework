package com.example.chernykhhomework.domain.repository

import com.example.chernykhhomework.domain.entity.Auth

interface AuthRepository {

    suspend fun login(auth: Auth): String

    suspend fun registration(auth: Auth): String

    suspend fun autoLogin(): String?
}