package com.droidcon.habitsync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.compose.koinInject
import com.droidcon.habitsync.domain.MongoRepository
import com.droidcon.habitsync.domain.model.Habit
import com.droidcon.habitsync.ui.screens.add_edit.AddEditHabitScreen
import com.droidcon.habitsync.ui.screens.detail.HabitDetailScreen
import com.droidcon.habitsync.ui.screens.home.HomeScreen
import com.droidcon.habitsync.ui.screens.motivation.MotivationScreen
import com.droidcon.habitsync.utils.Constant
import com.droidcon.habitsync.viewmodel.AddEditViewModel
import com.droidcon.habitsync.viewmodel.HabitDetailViewModel
import com.droidcon.habitsync.viewmodel.HomeViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    mongoRepository: MongoRepository = koinInject<MongoRepository>()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            val viewModel = viewModel { HomeViewModel(mongoRepository) }
            val homeViewState by viewModel.habits.collectAsState()
            HomeScreen(
                habits = homeViewState.habits,
                onClickDetailHabit = { habitId ->
                    navController.navigate(Screen.HabitDetail.passHabitId(habitId))
                },
                onClickEditHabit = { habitId ->
                    navController.navigate(Screen.AddEditHabit.passHabitId(habitId))
                },
                onClickAddHabit = { habitId ->
                    navController.navigate(Screen.AddEditHabit.passHabitId(habitId))
                },
                onClickDeleteHabit = { habitId ->
                    viewModel.deleteHabit(habitId)
                },
                onClickFilter = { filterType ->
                    viewModel.getFilteredHabits(filterType)
                },
                onClickSort = { sortType ->
                    viewModel.sortHabits(sortType = sortType)
                },
                onClickMotivation = { navController.navigate(Screen.Motivation.route) },
            )
        }
        composable(
            route = Screen.AddEditHabit.route,
            arguments = listOf(navArgument(Constant.HABIT_EDIT_ID) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            val viewModel = viewModel { AddEditViewModel(mongoRepository) }
            val habitId = it.arguments?.getString(Constant.HABIT_EDIT_ID)
            viewModel.getHabitById(habitId)
            val viewState by viewModel.viewState.collectAsState()

            AddEditHabitScreen(
                habit = viewState.habits.firstOrNull(),
                onSave = { habit ->
                    viewModel.addHabit(habit)
                    navController.navigate(Screen.Home.route)
                },
                onUpdate = { habit, habitId ->
                    viewModel.updateHabit(habit, habitId)
                    navController.navigate(Screen.Home.route)
                },
                onCancel = {
                    navController.navigate(Screen.Home.route)
                })
        }
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument(Constant.HABIT_DETAIL_ID) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            val viewModel = viewModel { HabitDetailViewModel(mongoRepository) }
            val habitId = it.arguments?.getString(Constant.HABIT_DETAIL_ID)
            viewModel.getHabitById(habitId)
            val viewState by viewModel.viewState.collectAsState()
            val habit = viewState.habits.firstOrNull() ?: Habit()

            HabitDetailScreen(
                habit = habit,
                onBack = {navController.popBackStack()}
            )
        }
        composable(route = Screen.Motivation.route) {
            MotivationScreen()
        }
    }
}
