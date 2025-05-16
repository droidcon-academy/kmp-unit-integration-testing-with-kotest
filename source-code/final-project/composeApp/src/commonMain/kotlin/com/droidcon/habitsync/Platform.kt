package com.droidcon.habitsync

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform