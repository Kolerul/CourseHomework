package com.example.chernykhhomework.domain.repository

import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanConditions
import com.example.chernykhhomework.domain.entity.LoanRequest

interface LoanRepository {

    suspend fun requestLoan(loan: LoanRequest): Loan

    suspend fun getLoanById(id: Int): Loan

    suspend fun getAllLoans(): List<Loan>

    suspend fun getLoanConditions(): LoanConditions
}