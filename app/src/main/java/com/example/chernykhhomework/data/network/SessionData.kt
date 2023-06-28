package com.example.chernykhhomework.data.network

import com.example.chernykhhomework.data.network.entity.AuthorizedUser
import com.example.chernykhhomework.domain.entity.Loan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionData @Inject constructor() {
    var currentSessionUser: AuthorizedUser? = null
    var currentSessionLoanList: List<Loan>? = null


    fun getLoanById(id: Int): Loan? =
        currentSessionLoanList?.first { x -> x.id == id }

    fun getToken(): String? {
        return currentSessionUser?.token
    }


}