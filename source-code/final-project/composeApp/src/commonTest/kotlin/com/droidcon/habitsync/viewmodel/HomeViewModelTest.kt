package com.droidcon.habitsync.viewmodel

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.droidcon.habitsync.utils.exerciseHabit
import com.droidcon.habitsync.utils.meditationHabit
import com.droidcon.habitsync.utils.readingHabit
import com.droidcon.habitsync.utils.studyHabit
import com.droidcon.habitsync.data.FakeMongoRepository
import com.droidcon.habitsync.ui.screens.home.FilterType
import com.droidcon.habitsync.ui.screens.home.SortType
import com.droidcon.habitsync.utils.ViewStatus


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest: StringSpec({
    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HomeViewModel

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    beforeTest {
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HomeViewModel(fakeRepository)
    }

    "should fetch habits successfully" {
        runTest {
            val habit1 = exerciseHabit
            val habit2 = readingHabit

            fakeRepository.addHabit(habit1)
            fakeRepository.addHabit(habit2)

            viewModel.getHabits()
            advanceUntilIdle()

            val latestState = viewModel.habits.first()

            latestState.habits shouldContain habit1
            latestState.habits shouldContain habit2
            latestState.viewSate shouldBe ViewStatus.SUCCESS
        }
    }

    "should handle failure when fetching habits" {
        runTest {
            fakeRepository.shouldFail = true

            viewModel.getHabits()
            advanceUntilIdle()

            val latestState = viewModel.habits.first()
            advanceUntilIdle()

            latestState.viewSate shouldBe ViewStatus.FAILED
            latestState.habits shouldBe emptyList()
            latestState.message shouldBe "Failed to fetch habits"
        }
    }


    "should delete a habit and update list" {
        runTest {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.deleteHabit(habit._id.toHexString())
            advanceUntilIdle()

            val latestState = viewModel.habits.first()
            latestState.habits.size shouldBe 0
        }
    }

    "should filter habits by weekly successfully" {
        runTest {
            val dailyHabit = exerciseHabit
            val weeklyHabit = readingHabit

            fakeRepository.addHabit(dailyHabit)
            fakeRepository.addHabit(weeklyHabit)
            advanceUntilIdle()

            viewModel.getHabits()
            advanceUntilIdle()

            viewModel.getFilteredHabits(FilterType.WEEKLY)
            advanceUntilIdle()

            val latestState = viewModel.habits.first()
            latestState.habits.size shouldBe 1
            latestState.habits.first().frequency shouldBe "Weekly"
        }
    }

    "should filter habits by daily successfully" {
        runTest {
            val dailyHabit = exerciseHabit
            val weeklyHabit = readingHabit

            fakeRepository.addHabit(dailyHabit)
            fakeRepository.addHabit(weeklyHabit)
            advanceUntilIdle()

            viewModel.getHabits()
            advanceUntilIdle()

            viewModel.getFilteredHabits(FilterType.DAILY)
            advanceUntilIdle()

            val latestState = viewModel.habits.first()
            latestState.habits.size shouldBe 1
            latestState.habits.first().frequency shouldBe "Daily"
        }
    }

    "should filter habits by All successfully" {
        runTest {
            val dailyHabit = exerciseHabit
            val weeklyHabit = readingHabit

            fakeRepository.addHabit(dailyHabit)
            fakeRepository.addHabit(weeklyHabit)
            advanceUntilIdle()

            viewModel.getHabits()
            advanceUntilIdle()

            viewModel.getFilteredHabits(FilterType.ALL)
            advanceUntilIdle()

            val latestState = viewModel.habits.first()
            latestState.habits.size shouldBe 2
        }
    }

    "should handle failure when filtering habits" {
        runTest {
            fakeRepository.shouldFail = true

            viewModel.getFilteredHabits(FilterType.WEEKLY)
            advanceUntilIdle()

            val latestState = viewModel.habits.first()

            latestState.viewSate shouldBe ViewStatus.FAILED
            latestState.habits shouldBe emptyList()
            latestState.message shouldBe "Failed to filter habits"
        }
    }

    "should sort habits in ascending order" {
        runTest {
            val meditationHabit = meditationHabit
            val studyHabit = studyHabit
            val exerciseHabit = exerciseHabit
            val readingHabit = readingHabit

            fakeRepository.addHabit(meditationHabit)
            fakeRepository.addHabit(studyHabit)
            fakeRepository.addHabit(exerciseHabit)
            fakeRepository.addHabit(readingHabit)
            advanceUntilIdle()
            viewModel.getHabits()
            advanceUntilIdle()

            viewModel.sortHabits(SortType.ASCENDING)
            advanceUntilIdle()

            val latestState = viewModel.habits.first()

            latestState.habits[0].name shouldBe exerciseHabit.name
            latestState.habits[1].name shouldBe meditationHabit.name
            latestState.habits[2].name shouldBe readingHabit.name
            latestState.habits[3].name shouldBe studyHabit.name
        }
    }

    "should sort habits in descending order" {
        runTest {
            val meditationHabit = meditationHabit
            val studyHabit = studyHabit
            val exerciseHabit = exerciseHabit
            val readingHabit = readingHabit

            fakeRepository.addHabit(meditationHabit)
            fakeRepository.addHabit(studyHabit)
            fakeRepository.addHabit(exerciseHabit)
            fakeRepository.addHabit(readingHabit)
            advanceUntilIdle()
            viewModel.getHabits()
            advanceUntilIdle()

            viewModel.sortHabits(SortType.DESCENDING)
            advanceUntilIdle()

            val latestState = viewModel.habits.first()

            latestState.habits[0].name shouldBe studyHabit.name
            latestState.habits[1].name shouldBe readingHabit.name
            latestState.habits[2].name shouldBe meditationHabit.name
            latestState.habits[3].name shouldBe exerciseHabit.name
        }
    }

})

