package com.example.chernykhhomework.data.repositoryImpl

import android.util.Log
import android.util.NoSuchPropertyException
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.LoansDataSourceApi
import com.example.chernykhhomework.data.network.entity.Loan
import com.example.chernykhhomework.data.network.entity.LoanConditions
import com.example.chernykhhomework.data.network.entity.LoanRequest
import com.example.chernykhhomework.domain.repository.LoanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoanRepositoryImpl @Inject constructor(
    private val retrofitService: LoansDataSourceApi,
    private val sessionData: SessionData,
    private val dispatcher: CoroutineDispatcher
) : LoanRepository {

    override suspend fun requestLoan(loan: LoanRequest): Loan = withContext(dispatcher) {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = retrofitService.postLoan(token, loan)
        Log.d("LoanRepositoryImpl", response.toString())
        response
    }

    override suspend fun getLoanById(id: Int): Loan = withContext(dispatcher) {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = sessionData.getLoanById(id) ?: retrofitService.getLoanById(token, id)
        Log.d("LoanRepositoryImpl", response.toString())
        response
    }

    override suspend fun getAllLoans(usePreferredSource: Boolean): List<Loan> =
        withContext(dispatcher) {
            val token =
                sessionData.getToken() ?: throw NoSuchElementException("Token not available")
            val loanList = if (usePreferredSource)
                sessionData.currentSessionLoanList
                    ?: throw NoSuchPropertyException("There no cache data")
            else
                retrofitService.getAllLoans(token)
            Log.d("LoanRepositoryImpl", loanList.toString())
            sessionData.currentSessionLoanList = loanList
            loanList
        }

    override suspend fun getLoanConditions(): LoanConditions = withContext(dispatcher) {
        Log.d("LoanRepositoryImpl", sessionData.toString())
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = retrofitService.getConditions(token)
        Log.d("LoanRepositoryImpl", response.toString())
        response
    }

}