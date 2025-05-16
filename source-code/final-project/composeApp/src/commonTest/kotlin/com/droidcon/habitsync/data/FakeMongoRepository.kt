package com.droidcon.habitsync.data

import com.droidcon.habitsync.domain.MongoRepository
import com.droidcon.habitsync.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mongodb.kbson.ObjectId

class FakeMongoRepository : MongoRepository {

    var habitList = mutableListOf<Habit>()
    var shouldFail = false

    override fun configureTheRealm() {}

    override suspend fun addHabit(habit: Habit) {
        if (shouldFail) throw Exception("Failed to add habit")
        habitList.add(habit)
        getHabits()
    }

    override fun getHabits(): Flow<Result<List<Habit>>> {
        return flow {
            if (shouldFail) {
                emit(Result.failure(Exception("Failed to fetch habits")))
            } else {
                emit(Result.success(habitList.toList()))
            }
        }
    }

    override fun getHabitById(habitId: String): Flow<Result<List<Habit>>> {
        return flow {
            if (shouldFail) {
                emit(Result.failure(Exception("Failed to fetch habit by ID")))
            } else {
                emit(Result.success(habitList.filter { it._id == ObjectId(habitId) }))
            }
        }
    }

    override suspend fun updateHabit(habit: Habit, habitId: String) {
        if (shouldFail) throw Exception("Failed to update habit")
        val index = habitList.indexOfFirst { it._id == ObjectId(habitId) }
        if (index != -1) {
            habitList[index] = habit
        }
    }


    override suspend fun deleteHabit(habitId: String) {
        habitList = habitList.filter { it._id != ObjectId(habitId) }.toMutableList()
    }

    override fun getFilteredHabits(filter: String): Flow<Result<List<Habit>>> {
        return flow {
            if (shouldFail) {
                emit(Result.failure(Exception("Failed to filter habits")))
            } else {
                if(filter == "All"){
                    emit(Result.success(habitList.toList()))
                }
                emit(Result.success(habitList.filter { it.frequency.equals(filter, ignoreCase = true) }))
            }
        }
    }
}