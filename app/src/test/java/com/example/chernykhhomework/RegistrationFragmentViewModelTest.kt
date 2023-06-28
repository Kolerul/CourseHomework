package com.example.chernykhhomework

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.domain.entity.Auth
import com.example.chernykhhomework.domain.repository.AuthRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import com.example.chernykhhomework.presentation.viewmodel.RegistrationFragmentViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.RuntimeException

@ExtendWith(InstantTaskExecutorExtension::class)
class RegistrationFragmentViewModelTest {
    private val authRepository: AuthRepository = mock()

    private val viewModel by lazy {
        RegistrationFragmentViewModel(
            authRepository
        )
    }

    val auth = Auth(
        "John",
        "123"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mainThreadSurrogate = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `WHEN registration with name John and password 123 EXPECT Success(John, true)`() =
        runTestWithArch {
            whenever(authRepository.registration(auth)).thenReturn("John")

            viewModel.register("John", "123")
            val expected = RegisterUIState.Success("John", true)
            val actual = viewModel.uiState.value
            assertEquals(expected, actual)
        }

    @Test
    fun `WHEN login with name John and password 123 EXPECT Success(John, false)`() =
        runTestWithArch {
            whenever(authRepository.login(auth)).thenReturn("John")

            viewModel.logIn("John", "123")
            val expected = RegisterUIState.Success("John", false)
            val actual = viewModel.uiState.value
            assertEquals(expected, actual)
        }

    @Test
    fun `WHEN autoLogin EXPECT Success(John, false)`() = runTestWithArch {
        whenever(authRepository.autoLogin()).thenReturn("John")

        viewModel.autoLogIn()
        val expected = RegisterUIState.Success("John", false)
        val actual = viewModel.uiState.value
        assertEquals(expected, actual)
    }

    @Test
    fun `WHEN login John 111 EXPECT Error(Unknown error)`() = runTestWithArch {
        whenever(authRepository.login(Auth("John", "111"))).thenThrow(RuntimeException())

        viewModel.logIn("John", "111")
        val expected =
            RegisterUIState.Error(
                ErrorWrapper(
                    R.string.unknown_error,
                    RuntimeException::class.java,
                    "null"
                )
            )
        val actual = viewModel.uiState.value
        assertEquals(expected, actual)
    }

    private fun runTestWithArch(testBody: suspend TestScope.() -> Unit) = runTest {
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            })

        this.testBody()

        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}