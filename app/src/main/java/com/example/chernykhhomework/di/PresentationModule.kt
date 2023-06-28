package com.example.chernykhhomework.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chernykhhomework.presentation.viewmodel.RegistrationFragmentViewModel
import com.example.chernykhhomework.presentation.ViewModelFactory
import com.example.chernykhhomework.presentation.viewmodel.LoanFragmentViewModel
import com.example.chernykhhomework.presentation.viewmodel.LoansListFragmentViewModel
import com.example.chernykhhomework.presentation.viewmodel.NewLoanFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@Module
interface PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationFragmentViewModel::class)
    fun bindRegistrationFragmentViewModel(viewModel: RegistrationFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewLoanFragmentViewModel::class)
    fun bindNewLoanFragmentViewModel(viewModel: NewLoanFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoansListFragmentViewModel::class)
    fun bindLoansListFragmentViewModel(viewModel: LoansListFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoanFragmentViewModel::class)
    fun bindLoanFragmentViewModel(viewModel: LoanFragmentViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


    companion object {
        @Provides
        fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    }

}