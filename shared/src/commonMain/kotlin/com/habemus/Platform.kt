package com.habemus

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
