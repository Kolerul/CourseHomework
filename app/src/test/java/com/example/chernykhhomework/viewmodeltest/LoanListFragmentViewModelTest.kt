package com.example.chernykhhomework.viewmodeltest

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.InstantTaskExecutorExtension
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanState
import com.example.chernykhhomework.domain.repository.LoanRepository
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import com.example.chernykhhomework.presentation.viewmodel.LoansListFragmentViewModel
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

@ExtendWith(InstantTaskExecutorExtension::class)
class LoanListFragmentViewModelTest {

    private val loanRepository: LoanRepository = mock()

    private val viewModel by lazy {
        LoansListFragmentViewModel(
            loanRepository
        )
    }

    private val loan1 = Loan(1000, "", "A", 1, "", 4.0, 1, "", LoanState.APPROVED)
    private val loan2 = Loan(1000, "", "B", 2, "", 4.0, 2, "", LoanState.REGISTERED)
    private val loan3 = Loan(1000, "", "C", 3, "", 4.0, 3, "", LoanState.APPROVED)
    private val loan4 = Loan(1000, "", "D", 4, "", 4.0, 4, "", LoanState.REGISTERED)

    private val listOfLoans = listOf(
        loan1,
        loan2,
        loan3,
        loan4
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
    fun `WHEN getAllLoans is successful EXPECT Success(listOfLoans)`() = runTestWithArch {
        whenever(loanRepository.getAllLoans()).thenReturn(listOfLoans)

        viewModel.getLoansList()
        val expected = LoansListUIState.Success(listOfLoans)
        val actual = viewModel.uiState.value
        assertEquals(expected, actual)
    }

    @Test
    fun `WHEN getAllLoans is not successful EXPECT Error(RuntimeException)`() = runTestWithArch {
        whenever(loanRepository.getAllLoans()).thenThrow(RuntimeException())

        viewModel.getLoansList()
        val expected =
            LoansListUIState.Error(
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