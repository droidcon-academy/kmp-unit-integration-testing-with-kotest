package com.droidcon.habitsync.navigation

import com.droidcon.habitsync.utils.Constant

sealed class Screen(val route: String){
    data object Home: Screen(route = "home_screen")
    data object AddEditHabit: Screen(route = "add_edit_habit/{${Constant.HABIT_EDIT_ID}}"){
        fun passHabitId(id: String?) = "add_edit_habit/$id"
    }
    data object HabitDetail: Screen(route = "habit_detail/{${Constant.HABIT_DETAIL_ID}}"){
        fun passHabitId(id: String?) = "habit_detail/$id"
    }
    data object Motivation: Screen(route = "motivation")
}