package com.app.takenote.utility


sealed class Response<T>
class Success<T>(val data: T) : Response<T>()
class Error<T>(val message: String) : Response<T>()