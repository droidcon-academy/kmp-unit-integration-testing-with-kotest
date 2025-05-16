package com.droidcon.habitsync.domain.model

import androidx.compose.runtime.Stable
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

@Stable
class Habit : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var description: String = ""
    var frequency: String = ""
    var category: String = ""
    var totalStreak: Int = 0
    var completedStreak: Int = 0
    var completed: Boolean = false
}