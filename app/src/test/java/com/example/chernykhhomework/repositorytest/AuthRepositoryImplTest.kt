package com.example.chernykhhomework.repositorytest

import android.content.Context
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.InstantTaskExecutorExtension
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.AuthorizationApi
import com.example.chernykhhomework.data.repositoryImpl.AuthRepositoryImpl
import com.example.chernykhhomework.domain.entity.Auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock

//Not implemented
@ExtendWith(InstantTaskExecutorExtension::class)
class AuthRepositoryImplTest {

    private val context: Context = mock()
    private val retrofitService: AuthorizationApi = mock()
    private val sessionData = SessionData()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()

    private val authRepository = AuthRepositoryImpl(
        retrofitService,
        sessionData,
        context,
        dispatcher
    )

    private val auth = Auth(
        name = "John",
        password = "123"
    )

    private val response: ResponseBody = mock()


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