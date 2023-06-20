package com.example.chernykhhomework.data.network.interceptor

import android.content.Context
import android.widget.Toast
import com.example.chernykhhomework.R
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LogInErrorInterceptor @Inject constructor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            400 -> {
                makeToast(context.getString(R.string.login_registration_error))
            }

            404 -> {
                makeToast(context.getString(R.string.login_or_password_error))
            }
        }
        return response
    }

    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}