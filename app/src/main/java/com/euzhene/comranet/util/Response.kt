package com.euzhene.comranet.util

sealed class Response<T>(
    val data: T? = null,
    val error: String? = null
) {
    class Success<T>(data: T) : Response<T>(data)
    class Loading<T>(data: T? = null) : Response<T>(data)
    class Error<T>(error: String) : Response<T>(error = error)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data=$data]"
            is Error -> "Error [exception = $error]"
            is Loading -> "Loading"
        }
    }
}