package com.droidcon.habitsync.viewmodel

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import com.droidcon.habitsync.utils.exerciseHabit
import com.droidcon.habitsync.utils.meditationHabit
import com.droidcon.habitsync.utils.readingHabit
import com.droidcon.habitsync.utils.studyHabit
import com.droidcon.habitsync.data.FakeMongoRepository
import com.droidcon.habitsync.ui.screens.home.FilterType
import com.droidcon.habitsync.ui.screens.home.SortType
import com.droidcon.habitsync.utils.ViewStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler


@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class HomeViewModelTest : FunSpec({
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

    test("should fetch habits successfully").config(coroutineTestScope = true) {
        val habit1 = exerciseHabit
        val habit2 = readingHabit

        fakeRepository.addHabit(habit1)
        fakeRepository.addHabit(habit2)

        viewModel.getHabits()
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()

        latestState.habits shouldContain habit1
        latestState.habits shouldContain habit2
        latestState.viewState shouldBe ViewStatus.SUCCESS
    }

    test("should handle failure when fetching habits").config(coroutineTestScope = true) {
        fakeRepository.shouldFail = true

        viewModel.getHabits()
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()
        testCoroutineScheduler.advanceUntilIdle()

        latestState.viewState shouldBe ViewStatus.FAILED
        latestState.habits shouldBe emptyList()
        latestState.message shouldBe "Failed to fetch habits"
    }


    test("should delete a habit and update list").config(coroutineTestScope = true) {
        val habit = exerciseHabit
        fakeRepository.addHabit(habit)

        viewModel.deleteHabit(habit._id.toHexString())
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()
        latestState.habits.size shouldBe 0
    }

    test("should filter habits by weekly successfully").config(coroutineTestScope = true) {
        val dailyHabit = exerciseHabit
        val weeklyHabit = readingHabit

        fakeRepository.addHabit(dailyHabit)
        fakeRepository.addHabit(weeklyHabit)
        testCoroutineScheduler.advanceUntilIdle()

        viewModel.getFilteredHabits(FilterType.WEEKLY)
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()
        latestState.habits.size shouldBe 1
        latestState.habits.first().frequency shouldBe "Weekly"
    }

    test("should filter habits by daily successfully").config(coroutineTestScope = true) {
        val dailyHabit = exerciseHabit
        val weeklyHabit = readingHabit

        fakeRepository.addHabit(dailyHabit)
        fakeRepository.addHabit(weeklyHabit)
        testCoroutineScheduler.advanceUntilIdle()

        viewModel.getFilteredHabits(FilterType.DAILY)
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()
        latestState.habits.size shouldBe 1
        latestState.habits.first().frequency shouldBe "Daily"
    }

    test("should filter habits by All successfully").config(coroutineTestScope = true) {
        val dailyHabit = exerciseHabit
        val weeklyHabit = readingHabit

        fakeRepository.addHabit(dailyHabit)
        fakeRepository.addHabit(weeklyHabit)
        testCoroutineScheduler.advanceUntilIdle()

        viewModel.getFilteredHabits(FilterType.ALL)
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()
        latestState.habits.size shouldBe 2
    }

    test("should handle failure when filtering habits").config(coroutineTestScope = true) {
        fakeRepository.shouldFail = true

        viewModel.getFilteredHabits(FilterType.WEEKLY)
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()

        latestState.viewState shouldBe ViewStatus.FAILED
        latestState.habits shouldBe emptyList()
        latestState.message shouldBe "Failed to filter habits"
    }

    test("should sort habits in ascending order").config(coroutineTestScope = true) {
        val meditationHabit = meditationHabit
        val studyHabit = studyHabit
        val exerciseHabit = exerciseHabit
        val readingHabit = readingHabit

        fakeRepository.addHabit(meditationHabit)
        fakeRepository.addHabit(studyHabit)
        fakeRepository.addHabit(exerciseHabit)
        fakeRepository.addHabit(readingHabit)
        testCoroutineScheduler.advanceUntilIdle()
        viewModel.getHabits()
        testCoroutineScheduler.advanceUntilIdle()

        viewModel.sortHabits(SortType.ASCENDING)
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()

        latestState.habits[0].name shouldBe exerciseHabit.name
        latestState.habits[1].name shouldBe meditationHabit.name
        latestState.habits[2].name shouldBe readingHabit.name
        latestState.habits[3].name shouldBe studyHabit.name
    }

    test("should sort habits in descending order").config(coroutineTestScope = true) {
        val meditationHabit = meditationHabit
        val studyHabit = studyHabit
        val exerciseHabit = exerciseHabit
        val readingHabit = readingHabit

        fakeRepository.addHabit(meditationHabit)
        fakeRepository.addHabit(studyHabit)
        fakeRepository.addHabit(exerciseHabit)
        fakeRepository.addHabit(readingHabit)
        testCoroutineScheduler.advanceUntilIdle()
        viewModel.getHabits()
        testCoroutineScheduler.advanceUntilIdle()

        viewModel.sortHabits(SortType.DESCENDING)
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.habits.first()

        latestState.habits[0].name shouldBe studyHabit.name
        latestState.habits[1].name shouldBe readingHabit.name
        latestState.habits[2].name shouldBe meditationHabit.name
        latestState.habits[3].name shouldBe exerciseHabit.name
    }

})
