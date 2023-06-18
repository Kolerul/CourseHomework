package com.example.chernykhhomework.domain.repository

import com.example.chernykhhomework.data.network.entity.Auth

interface AuthRepository {

    suspend fun login(auth: Auth): String

    suspend fun registration(auth: Auth)
}