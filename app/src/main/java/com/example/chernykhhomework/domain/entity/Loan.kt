package com.example.chernykhhomework.domain.entity


data class Loan(
    val amount: Long,
    val date: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val percent: Double,
    val period: Int,
    val phoneNumber: String,
    val state: LoanState
)