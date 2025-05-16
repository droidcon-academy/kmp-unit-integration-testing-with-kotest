package com.droidcon.habitsync.di

import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.droidcon.habitsync.data.MongoRepositoryImpl
import com.droidcon.habitsync.domain.MongoRepository

val habitModules = module {
    single<MongoRepository> { MongoRepositoryImpl() }
}

fun initializeKoin() {
    startKoin {
        modules(habitModules)
    }
}