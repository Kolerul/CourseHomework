package com.example.chernykhhomework.data.repositoryImpl

import android.util.NoSuchPropertyException
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.LoansDataSourceApi
import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanConditions
import com.example.chernykhhomework.domain.entity.LoanRequest
import com.example.chernykhhomework.domain.repository.LoanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
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
        response
    }

    override suspend fun getLoanById(id: Int): Loan = withContext(dispatcher) {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = sessionData.getLoanById(id) ?: retrofitService.getLoanById(token, id)
        response
    }

    override suspend fun getAllLoans(): List<Loan> =
        withContext(dispatcher) {
            val token =
                sessionData.getToken() ?: throw NoSuchElementException("Token not available")
            var loanList: List<Loan>
            try {
                loanList = retrofitService.getAllLoans(token)
                sessionData.currentSessionLoanList = loanList
            } catch (e: UnknownHostException) {
                val list = sessionData.currentSessionLoanList
                if (list != null) {
                    loanList = list
                } else {
                    throw NoSuchPropertyException("No cached")
                }
            }
            loanList
        }

    override suspend fun getLoanConditions(): LoanConditions = withContext(dispatcher) {
        val token = sessionData.getToken() ?: throw NoSuchElementException("Token not available")
        val response = retrofitService.getConditions(token)
        response
    }

}