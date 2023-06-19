package com.example.chernykhhomework.di

import android.content.Context
import com.example.chernykhhomework.presentation.viewmodel.RegistrationFragmentViewModel
import com.example.chernykhhomework.presentation.ViewModelFactory
import com.example.chernykhhomework.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        NetworkModule::class,
        PresentationModule::class,
        DomainModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragmentViewModel: RegistrationFragmentViewModel)

    fun viewModelsFactory(): ViewModelFactory
}