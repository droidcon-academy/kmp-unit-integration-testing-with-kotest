package com.droidcon.habitsync

import com.droidcon.habitsync.data.FakeMongoRepository
import com.droidcon.habitsync.utils.ViewStatus
import com.droidcon.habitsync.utils.exerciseHabit
import com.droidcon.habitsync.viewmodel.HabitDetailViewModel
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelAnnotationSpecTest : AnnotationSpec() {

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    @AfterEach
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchHabitByIdSuccessfully() = runTest {
        val habit = exerciseHabit
        fakeRepository.addHabit(habit)

        viewModel.getHabitById(habit._id.toHexString())
        advanceUntilIdle()

        val latestState = viewModel.viewState.first()

        latestState.viewSate shouldBe ViewStatus.SUCCESS
        latestState.habits.firstOrNull() shouldBe habit
    }
}
