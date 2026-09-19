package com.gymtracker.data.auth

expect class TokenStore {
    fun getToken(): String?
    fun setToken(token: String)
    fun getRefreshToken(): String?
    fun setRefreshToken(token: String)
    fun getUserData(): String?
    fun setUserData(data: String)
    fun clearAll()
}
