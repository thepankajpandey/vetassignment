package com.example.vetclinic.util

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T, val code: Int) : NetworkResult<T>()
    data class Error(val code: Int?, val message: String) : NetworkResult<Nothing>()
}