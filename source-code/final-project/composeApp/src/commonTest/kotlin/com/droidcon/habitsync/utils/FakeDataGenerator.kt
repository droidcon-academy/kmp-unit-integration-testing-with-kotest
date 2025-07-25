package com.droidcon.habitsync.utils

import com.droidcon.habitsync.domain.model.Habit


fun createHabit(
    name: String,
    description: String,
    frequency: String,
    category: String,
    streak: Int,
    completeDayOrWeek: Int,
    completed: Boolean
): Habit {
    return Habit().apply {
        this._id = org.mongodb.kbson.ObjectId()
        this.name = name
        this.description = description
        this.frequency = frequency
        this.category = category
        this.totalStreak = streak
        this.completedStreak = completeDayOrWeek
        this.completed = completed
    }
}

val exerciseHabit = createHabit(
    name = "Exercise",
    description = "A 30-minute workout every morning",
    frequency = "Daily",
    category = "Health",
    streak = 10,
    completeDayOrWeek = 7,
    completed = false
)

val readingHabit = createHabit(
    name = "Reading",
    description = "Read 20 pages of a book every night",
    frequency = "Weekly",
    category = "Personal Growth",
    streak = 15,
    completeDayOrWeek = 7,
    completed = false
)

val studyHabit = createHabit(
    name = "Sunday Meal Prep",
    description = "Prepare meals for the upcoming week every Sunday",
    frequency = "Weekly",
    category = "Productivity",
    streak = 4,
    completeDayOrWeek = 4,
    completed = true
)

val meditationHabit = createHabit(
    name = "Meditation",
    description = "Practice mindfulness meditation for 10 minutes",
    frequency = "Daily",
    category = "Mental Wellness",
    streak = 20,
    completeDayOrWeek = 7,
    completed = false
)
