package com.droidcon.habitsync.data

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.BsonObjectId.Companion.invoke
import com.droidcon.habitsync.domain.MongoRepository
import com.droidcon.habitsync.domain.model.Habit
import com.droidcon.habitsync.utils.Constant
import com.droidcon.habitsync.utils.toResultFlow
import org.mongodb.kbson.ObjectId

class MongoRepositoryImpl : MongoRepository {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(Habit::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    override suspend fun addHabit(habit: Habit) {
        realm?.writeBlocking {
            copyToRealm(habit)
        }
    }

    override fun getHabits(): Flow<Result<List<Habit>>> {
        val result: RealmResults<Habit>? =
            realm?.query(Habit::class)?.find()
        return result.toResultFlow()
    }

    override fun getHabitById(habitId: String): Flow<Result<List<Habit>>> {
        val result: RealmResults<Habit>? =
            realm?.query(Habit::class, "_id == $0", ObjectId(habitId))?.find()
        return result.toResultFlow()
    }

    override fun getFilteredHabits(
        filter: String
    ): Flow<Result<List<Habit>>> {
        if(filter == Constant.ALL){
            val result: RealmResults<Habit>? =
                realm?.query(Habit::class)?.find()
            return result.toResultFlow()
        }
        val result: RealmResults<Habit>? =
            realm?.query(Habit::class, "frequency == $0", filter )?.find()
        return result.toResultFlow()
    }

    override suspend fun updateHabit(habit: Habit, habitId: String) {
        realm?.write {
            val existingHabit =
                this.query(Habit::class, "_id == $0", ObjectId(habitId)).find().firstOrNull()
            existingHabit?.apply {
                name = habit.name
                description = habit.description
                frequency = habit.frequency
                category = habit.category
                totalStreak = habit.totalStreak
                completedStreak = habit.completedStreak
                completed = habit.completed
            }
        }
    }

    override suspend fun deleteHabit(habitId: String) {
        realm?.writeBlocking {
            val habit = query(Habit::class, "_id == $0", ObjectId(habitId))
                .first()
                .find()
            habit?.let { delete(it) }
        }
    }
}