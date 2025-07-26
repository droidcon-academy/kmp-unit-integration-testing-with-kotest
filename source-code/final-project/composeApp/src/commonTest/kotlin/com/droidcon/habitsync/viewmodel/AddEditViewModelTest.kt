package com.droidcon.habitsync.viewmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
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
import com.droidcon.habitsync.data.FakeMongoRepository
import com.droidcon.habitsync.utils.ViewStatus
import com.droidcon.habitsync.utils.createHabit
import com.droidcon.habitsync.utils.studyHabit
import io.kotest.core.test.testCoroutineScheduler

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddHabitNestedTest : DescribeSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
    }
    describe("Adding Habit"){
        context("Add Habit Successfully") {
            it("should mark habit as completed and update the repository")
                .config(coroutineTestScope = true) {
                    val habit = studyHabit
                    viewModel.addHabit(habit)

                    viewModel.getHabitById(habit._id.toHexString())
                    testCoroutineScheduler.advanceUntilIdle()

                    val latestState = viewModel.viewState.first()
                    latestState.viewState shouldBe ViewStatus.SUCCESS
                    latestState.habits.firstOrNull()?.completed shouldBe true
                }

            it("should not mark habit as completed if streak is incomplete")
                .config(coroutineTestScope = true) {
                    val habit = exerciseHabit
                    viewModel.addHabit(habit)

                    viewModel.getHabitById(habit._id.toHexString())
                    testCoroutineScheduler.advanceUntilIdle()

                    val latestState = viewModel.viewState.first()
                    latestState.viewState shouldBe ViewStatus.SUCCESS
                    latestState.habits.firstOrNull()?.completed shouldBe false
                }
        }
        context("Failed to Add Habit") {
            it("should handle failure when repository fails to save the habit").config(
                coroutineTestScope = true
            ) {
                val habit = exerciseHabit
                fakeRepository.shouldFail = true
                viewModel.addHabit(habit)

                viewModel.getHabitById(habit._id.toHexString())
                testCoroutineScheduler.advanceUntilIdle()

                val latestState = viewModel.viewState.first()
                latestState.viewState shouldBe ViewStatus.FAILED
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddHabitDynamicTest : DescribeSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    describe("AddEditViewModel addHabit logic") {

        val testCases = listOf(
            Triple("Should be completed", 5, 5),
            Triple("Should not be completed", 2, 5),
            Triple("Completed at boundary", 3, 3),
            Triple("Still incomplete", 1, 4)
        )

        testCases.forEach { (description, completedStreak, totalStreak) ->
            it("Habit with completedStreak=$completedStreak and totalStreak=$totalStreak: $description")
                .config(coroutineTestScope = true) {
                    val habit = createHabit(
                        name = "Dynamic Habit",
                        description = "Varies by test case",
                        frequency = "Daily",
                        category = "Health",
                        completeDayOrWeek = completedStreak,
                        streak = totalStreak,
                        completed = false
                    )

                    viewModel.addHabit(habit)
                    viewModel.getHabitById(habit._id.toHexString())
                    testCoroutineScheduler.advanceUntilIdle()

                    val latestState = viewModel.viewState.first()
                    val expectedCompletion = completedStreak >= totalStreak

                    latestState.habits.firstOrNull() shouldBe habit
                    latestState.habits.firstOrNull()?.completed shouldBe expectedCompletion
                }
        }
    }
})


@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelStringSpecTest : StringSpec({
    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "should fetch habit by ID successfully" {
        runTest {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            advanceUntilIdle()

            val latestState = viewModel.viewState.first()

            latestState.viewState shouldBe ViewStatus.SUCCESS
            latestState.habits.firstOrNull()?.shouldBe(habit)
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelFuncSpecTest : FunSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("should fetch habit by ID successfully in FunSpec")
        .config(coroutineTestScope = true) {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            testCoroutineScheduler.advanceUntilIdle()

            val latestState = viewModel.viewState.first()

            latestState.viewState shouldBe ViewStatus.SUCCESS
            latestState.habits.firstOrNull() shouldBe habit
        }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelBehaviorSpecTest : BehaviorSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    given("an AddEditViewModel") {
        `when`("fetching a habit by ID successfully") {
            then("it should return the correct habit").config(coroutineTestScope = true) {
                val habit = exerciseHabit
                fakeRepository.addHabit(habit)

                viewModel.getHabitById(habit._id.toHexString())
                testCoroutineScheduler.advanceUntilIdle()

                val latestState = viewModel.viewState.first()

                latestState.viewState shouldBe ViewStatus.SUCCESS
                latestState.habits.first() shouldBe habit
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelDescribeSpecTest : DescribeSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    describe("AddEditViewModel") {
        context("Fetching Habit By ID") {
            it("should return habit successfully").config(coroutineTestScope = true) {
                val habit = exerciseHabit
                fakeRepository.addHabit(habit)

                viewModel.getHabitById(habit._id.toHexString())
                testCoroutineScheduler.advanceUntilIdle()

                val latestState = viewModel.viewState.first()

                latestState.viewState shouldBe ViewStatus.SUCCESS
                latestState.habits.firstOrNull() shouldBe habit

            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelShouldSpecTest : ShouldSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    should("fetch habit by ID successfully").config(coroutineTestScope = true) {
        val habit = exerciseHabit
        fakeRepository.addHabit(habit)

        viewModel.getHabitById(habit._id.toHexString())
        testCoroutineScheduler.advanceUntilIdle()

        val latestState = viewModel.viewState.first()

        latestState.viewState shouldBe ViewStatus.SUCCESS
        latestState.habits.firstOrNull() shouldBe habit
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelFreeSpecTest : FreeSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    "AddEditViewModel" - {
        "Fetching Habit By ID" - {
            "should fetch habit successfully".config(coroutineTestScope = true) {
                val habit = exerciseHabit
                fakeRepository.addHabit(habit)

                viewModel.getHabitById(habit._id.toHexString())
                testCoroutineScheduler.advanceUntilIdle()

                val latestState = viewModel.viewState.first()
                latestState.viewState shouldBe ViewStatus.SUCCESS
                latestState.habits.first() shouldBe habit
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelExpectSpecTest : ExpectSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    context("Fetching Habit By ID") {
        expect("it should return habit successfully").config(coroutineTestScope = true) {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            testCoroutineScheduler.advanceUntilIdle()

            val latestState = viewModel.viewState.first()
            latestState.viewState shouldBe ViewStatus.SUCCESS
            latestState.habits.first() shouldBe habit
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class AddEditViewModelFeatureSpecTest : FeatureSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: AddEditViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    feature("Fetching Habit By ID") {
        scenario("Should return habit successfully").config(coroutineTestScope = true) {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            testCoroutineScheduler.advanceUntilIdle()

            val latestState = viewModel.viewState.first()
            latestState.viewState shouldBe ViewStatus.SUCCESS
            latestState.habits.first() shouldBe habit
        }
    }
})
