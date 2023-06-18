package com.example.chernykhhomework.di

import com.example.chernykhhomework.data.repositoryImpl.AuthRepositoryImpl
import com.example.chernykhhomework.data.repositoryImpl.LoanRepositoryImpl
import com.example.chernykhhomework.domain.repository.AuthRepository
import com.example.chernykhhomework.domain.repository.LoanRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindLoanRepository(repository: LoanRepositoryImpl): LoanRepository
}