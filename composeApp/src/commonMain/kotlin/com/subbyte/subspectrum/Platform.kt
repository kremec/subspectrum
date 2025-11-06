package com.subbyte.subspectrum

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform