package com.example.chernykhhomework.di

import android.app.Application
import com.example.chernykhhomework.presentation.ViewModelFactory
import com.example.chernykhhomework.ui.MainActivity
import com.example.chernykhhomework.ui.fragments.LoanFragment
import com.example.chernykhhomework.ui.fragments.LoansListFragment
import com.example.chernykhhomework.ui.fragments.NewLoanFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        NetworkModule::class,
        PresentationModule::class,
        DomainModule::class,
        ApplicationModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: LoansListFragment)

    fun inject(fragment: NewLoanFragment)

    fun inject(fragment: LoanFragment)

    fun viewModelsFactory(): ViewModelFactory
}