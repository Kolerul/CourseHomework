package com.example.chernykhhomework.data.network.api

import com.example.chernykhhomework.data.network.entity.Loan
import com.example.chernykhhomework.data.network.entity.LoanConditions
import com.example.chernykhhomework.data.network.entity.LoanRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface LoansDataSourceApi {

    @POST("loans")
    suspend fun postLoan(
        @Header("Authorization") token: String,
        @Body loan: LoanRequest
    ): Loan

    @GET("loans/{id}")
    suspend fun getLoanById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Loan

    @GET("loans/all")
    suspend fun getAllLoans(
        @Header("Authorization") token: String
    ): List<Loan>

    @GET("loans/conditions")
    suspend fun getConditions(
        @Header("Authorization") token: String
    ): LoanConditions


    companion object {
        const val BASE_URL = "https://shiftlab.cft.ru:7777"
    }
}