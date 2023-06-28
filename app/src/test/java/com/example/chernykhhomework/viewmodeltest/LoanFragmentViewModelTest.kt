package com.example.chernykhhomework.viewmodeltest

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.InstantTaskExecutorExtension
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanState
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import com.example.chernykhhomework.presentation.viewmodel.LoanFragmentViewModel
import junit.framework.TestCase
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
import java.lang.RuntimeException

@ExtendWith(InstantTaskExecutorExtension::class)
class LoanFragmentViewModelTest {

    private val loanRepository: LoanRepository = mock()

    private val viewModel by lazy {
        LoanFragmentViewModel(
            loanRepository
        )
    }

    private val loan = Loan(
        777,
        "28.06.2023",
        "Artorias",
        1,
        "The Traveler of the Abyss",
        4.0,
        6,
        "759434",
        LoanState.APPROVED
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
        Dispatchers.resetMain()
    }

    @Test
    fun `WHEN getLoanById(1) is successful EXPECT Success(Loan)`() = runTestWithArch {
        whenever(loanRepository.getLoanById(1)).thenReturn(loan)

        viewModel.getLoanById(1)
        val expected = LoanUIState.Success(loan)
        val actual = viewModel.uiState.value
        TestCase.assertEquals(expected, actual)
    }

    @Test
    fun `WHEN getLoanById(1) is failure EXPECT Error()`() = runTestWithArch {
        whenever(loanRepository.getLoanById(1)).thenThrow(RuntimeException())

        viewModel.getLoanById(1)
        val expected = LoanUIState.Error(
            ErrorWrapper(R.string.unknown_error, RuntimeException::class.java, "null")
        )
        val actual = viewModel.uiState.value
        TestCase.assertEquals(expected, actual)
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