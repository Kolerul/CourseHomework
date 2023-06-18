package com.example.chernykhhomework.data.repositoryImpl

import android.util.Log
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.LoansDataSourceApi
import com.example.chernykhhomework.data.network.entity.Loan
import com.example.chernykhhomework.data.network.entity.LoanConditions
import com.example.chernykhhomework.data.network.entity.LoanRequest
import com.example.chernykhhomework.domain.repository.LoanRepository
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val retrofitService: LoansDataSourceApi,
    private val sessionData: SessionData
) : LoanRepository {

    override suspend fun requestLoan(loan: LoanRequest): Loan {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = retrofitService.postLoan(token, loan)
        Log.d("LoanRepositoryImpl", response.toString())
        return response
    }

    override suspend fun getLoanById(id: Int): Loan {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = sessionData.getLoanById(id) ?: retrofitService.getLoanById(token, id)
        Log.d("LoanRepositoryImpl", response.toString())
        return response
    }

    override suspend fun getAllLoans(): List<Loan> {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val loanList = retrofitService.getAllLoans(token)
        Log.d("LoanRepositoryImpl", loanList.toString())
        sessionData.currentSessionLoanList = loanList
        return loanList
    }

    override suspend fun getLoanConditions(): LoanConditions {
        Log.d("LoanRepositoryImpl", sessionData.toString())
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = retrofitService.getConditions(token)
        Log.d("LoanRepositoryImpl", response.toString())
        return response
    }

}