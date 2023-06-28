package com.example.chernykhhomework

import android.content.Context
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.example.chernykhhomework.data.network.SessionData
import com.example.chernykhhomework.data.network.api.AuthorizationApi
import com.example.chernykhhomework.data.repositoryImpl.AuthRepositoryImpl
import com.example.chernykhhomework.domain.entity.Auth
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

@ExtendWith(InstantTaskExecutorExtension::class)
class AuthRepositoryImplTest {

    val context: Context = mock()
    val retrofitService: AuthorizationApi = mock()
    val sessionData: SessionData = mock()
    val dispatcher = UnconfinedTestDispatcher()

    val authRepository = AuthRepositoryImpl(
        retrofitService,
        sessionData,
        context,
        dispatcher
    )

    val auth = Auth(
        name = "John",
        password = "123"
    )

    @Test
    fun `When registration normal auth EXPECT auth name`() = runTestWithArch {
        //whenever(retrofitService.login(auth)).thenReturn(ResponseBody().)
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