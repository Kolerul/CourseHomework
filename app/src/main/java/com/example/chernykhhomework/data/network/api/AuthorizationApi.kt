package com.example.chernykhhomework.data.network.api

import com.example.chernykhhomework.data.network.entity.Auth
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthorizationApi {

    @POST("login")
    suspend fun login(
        @Body auth: Auth
    ): ResponseBody

    @POST("registration")
    suspend fun registration(
        @Body auth: Auth
    ): ResponseBody
}