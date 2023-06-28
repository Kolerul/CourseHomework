package com.example.chernykhhomework.presentation.entity

data class ErrorWrapper(
    val errorCode: Int,
    val errorClass: Class<out Exception> = Exception::class.java,
    val errorMessage: String = ""
)