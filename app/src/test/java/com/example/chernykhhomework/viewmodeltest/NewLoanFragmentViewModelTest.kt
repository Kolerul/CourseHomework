package com.example.chernykhhomework.viewmodeltest

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.InstantTaskExecutorExtension
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanConditions
import com.example.chernykhhomework.domain.entity.LoanRequest
import com.example.chernykhhomework.domain.entity.LoanState
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import com.example.chernykhhomework.presentation.viewmodel.NewLoanFragmentViewModel
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
class NewLoanFragmentViewModelTest {

    private val loanRepository: LoanRepository = mock()

    private val viewModel by lazy {
        NewLoanFragmentViewModel(
            loanRepository
        )
    }

    private val loanCondition = LoanConditions(
        maxAmount = 100000,
        percent = 10.0,
        period = 100
    )

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

    private val requestLoan = LoanRequest(
        amount = 777,
        firstName = "Artorias",
        lastName = "The Traveler of the Abyss",
        period = 6,
        percent = 4.0,
        phoneNumber = "759434"
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
    fun `WHEN getConditions is successful EXPECT Success(Conditions)`() = runTestWithArch {
        whenever(loanRepository.getLoanConditions()).thenReturn(loanCondition)

        viewModel.conditionsRequest()
        val expected = NewLoanUIState.Success(loanCondition)
        val actual = viewModel.uiState.value
        TestCase.assertEquals(expected, actual)
    }

    @Test
    fun `WHEN requestLoan is successful EXPECT Success(null)`() = runTestWithArch {
        whenever(loanRepository.requestLoan(requestLoan)).thenReturn(loan)

        viewModel.newLoanRequest(requestLoan)
        val expected = NewLoanUIState.Success(null)
        val actual = viewModel.uiState.value
        TestCase.assertEquals(expected, actual)
    }

    @Test
    fun `WHEN requestLoan is failure EXPECT Error()`() = runTestWithArch {
        whenever(loanRepository.requestLoan(requestLoan)).thenThrow(RuntimeException())

        viewModel.newLoanRequest(requestLoan)
        val expected =
            NewLoanUIState.Error(
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