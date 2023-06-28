package com.example.chernykhhomework

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.LoansDataSourceApi
import com.example.chernykhhomework.data.network.entity.AuthorizedUser
import com.example.chernykhhomework.data.repositoryImpl.LoanRepositoryImpl
import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanConditions
import com.example.chernykhhomework.domain.entity.LoanRequest
import com.example.chernykhhomework.domain.entity.LoanState
import com.example.chernykhhomework.domain.repository.LoanRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(InstantTaskExecutorExtension::class)
class LoanRepositoryImplTest {
    private val retrofitService: LoansDataSourceApi = mock()

    private val sessionData = SessionData()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val loanRepository: LoanRepository by lazy {
        LoanRepositoryImpl(
            dispatcher = UnconfinedTestDispatcher(),
            retrofitService = retrofitService,
            sessionData = sessionData
        )
    }

    @Before
    fun init() {
        sessionData.currentSessionUser = AuthorizedUser(
            name = "Vovan",
            password = "Password",
            token = "Token"
        )
    }

    @After
    fun clear() {
        sessionData.currentSessionUser = null
        sessionData.currentSessionLoanList = null
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

    private val loanConditions = LoanConditions(
        11.0,
        7,
        1000
    )

    private val loanRequest = LoanRequest(
        amount = 777,
        firstName = "Artorias",
        lastName = "The Traveler of the Abyss",
        period = 6,
        percent = 4.0,
        phoneNumber = "759434"
    )

    @Test
    fun `WHEN token exist getAllLoans EXPECT List of 4 loans`() = runTestWithArch {
        whenever(retrofitService.getAllLoans("Token")).thenReturn(listOfLoans)

        val actual = loanRepository.getAllLoans().size
        val expected = 4
        assertEquals(expected, actual)
    }

    @Test
    fun `WHEN token exist getAllLoans EXPECT sessionData currentList is size listOfLoans`() =
        runTestWithArch {
            whenever(retrofitService.getAllLoans("Token")).thenReturn(listOfLoans)

            loanRepository.getAllLoans()

            val expected = listOfLoans.size
            val actual = sessionData.currentSessionLoanList?.size
            assertEquals(expected, actual)
        }

    @Test
    fun `WHEN token exist and sessionDataList not empty getLoanById 3 EXPECT List with firstname C`() =
        runTestWithArch {
            whenever(retrofitService.getAllLoans("Token")).thenReturn(listOfLoans)

            loanRepository.getAllLoans()

            val actual = loanRepository.getLoanById(3).firstName
            val expected = "C"
            assertEquals(expected, actual)
        }


    @Test
    fun `WHEN token exist and sessionDataList empty getLoanById 4 EXPECT List with state REGISTERED`() =
        runTestWithArch {
            whenever(retrofitService.getLoanById("Token", 4)).thenReturn(loan4)

            val actual = loanRepository.getLoanById(4).state
            val expected = LoanState.REGISTERED
            assertEquals(expected, actual)
        }

    @Test
    fun `WHEN token exist getConditions EXPECT percent 11D`() = runTestWithArch {
        whenever(retrofitService.getConditions("Token")).thenReturn(loanConditions)

        val actual = loanRepository.getLoanConditions().percent
        val expected = 11.0
        assertEquals(expected, actual)
    }

    @Test
    fun `WHEN token exist requestLoan EXPECT status APPROVED`() = runTestWithArch {
        whenever(retrofitService.postLoan("Token", loanRequest)).thenReturn(loan1)

        val actual = loanRepository.requestLoan(loanRequest).state
        val expected = LoanState.APPROVED
        assertEquals(expected, actual)
    }


    @Test
    fun `WHEN token absent postLoan EXPECT throw NoSuchElementException`() = runTestWithArch {
        whenever(retrofitService.postLoan("Token", loanRequest)).thenReturn(loan1)

        sessionData.currentSessionUser = null

        assertThrows<NoSuchElementException> {
            loanRepository.requestLoan(loanRequest)
        }
    }

    @Test
    fun `WHEN token absent getAllLoans EXPECT throw NoSuchElementException`() = runTestWithArch {
        whenever(retrofitService.getAllLoans("Token")).thenReturn(listOfLoans)

        sessionData.currentSessionUser = null

        assertThrows<NoSuchElementException> {
            loanRepository.getAllLoans()
        }
    }

    @Test
    fun `WHEN token absent getConditions EXPECT throw NoSuchElementException`() = runTestWithArch {
        whenever(retrofitService.getConditions("Token")).thenReturn(loanConditions)

        sessionData.currentSessionUser = null

        assertThrows<NoSuchElementException> {
            loanRepository.getLoanConditions()
        }
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