package com.droidcon.habitsync

import com.droidcon.habitsync.data.FakeMongoRepository
import com.droidcon.habitsync.utils.ViewStatus
import com.droidcon.habitsync.utils.exerciseHabit
import com.droidcon.habitsync.viewmodel.AddEditViewModel
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
    lateinit var viewModel: AddEditViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
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

        latestState.viewState shouldBe ViewStatus.SUCCESS
        latestState.habits.firstOrNull() shouldBe habit
    }
}
