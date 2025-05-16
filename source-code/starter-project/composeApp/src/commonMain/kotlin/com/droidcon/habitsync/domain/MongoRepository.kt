package com.droidcon.habitsync.domain

import kotlinx.coroutines.flow.Flow
import com.droidcon.habitsync.domain.model.Habit

interface MongoRepository {
    fun configureTheRealm()
    suspend fun addHabit(habit: Habit)
    fun getHabits(): Flow<Result<List<Habit>>>
    fun getHabitById(habitId: String): Flow<Result<List<Habit>>>
    fun getFilteredHabits(filter: String):Flow<Result<List<Habit>>>
    suspend fun updateHabit(habit: Habit, habitId: String)
    suspend fun deleteHabit(habitId: String)
}