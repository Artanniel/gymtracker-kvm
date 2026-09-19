package com.gymtracker

import android.content.Context
import com.gymtracker.data.auth.TokenStore

actual fun createPlatformTokenStore(): TokenStore {
    return TokenStore(gymtrackerContext)
}

lateinit var gymtrackerContext: Context
    private set

fun initGymtrackerContext(context: Context) {
    gymtrackerContext = context.applicationContext
}
