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

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelStringSpecTest : StringSpec({
    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

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
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    "should fetch habit by ID successfully" {
        runTest {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            advanceUntilIdle()

            val latestState = viewModel.viewState.first()

            latestState.viewSate shouldBe ViewStatus.SUCCESS
            latestState.habits.firstOrNull()?.shouldBe(habit)
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelFuncSpecTest : FunSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    test("should fetch habit by ID successfully in FunSpec") {
        runTest {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            advanceUntilIdle()

            val latestState = viewModel.viewState.first()

            latestState.viewSate shouldBe ViewStatus.SUCCESS
            latestState.habits.firstOrNull() shouldBe habit
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelBehaviorSpecTest : BehaviorSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    given("an AddEditViewModel") {
        `when`("fetching a habit by ID successfully") {
            then("it should return the correct habit") {
                runTest {
                    val habit = exerciseHabit
                    fakeRepository.addHabit(habit)

                    viewModel.getHabitById(habit._id.toHexString())
                    advanceUntilIdle()

                    val latestState = viewModel.viewState.first()

                    latestState.viewSate shouldBe ViewStatus.SUCCESS
                    latestState.habits.first() shouldBe habit
                }
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelDescribeSpecTest : DescribeSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    describe("AddEditViewModel") {
        context("Fetching Habit By ID") {
            it("should return habit successfully") {
                runTest {
                    val habit = exerciseHabit
                    fakeRepository.addHabit(habit)

                    viewModel.getHabitById(habit._id.toHexString())
                    advanceUntilIdle()

                    val latestState = viewModel.viewState.first()

                    latestState.viewSate shouldBe ViewStatus.SUCCESS
                    latestState.habits.firstOrNull() shouldBe habit
                }
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelShouldSpecTest : ShouldSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterTest {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    should("fetch habit by ID successfully") {
        runTest {
            val habit = exerciseHabit
            fakeRepository.addHabit(habit)

            viewModel.getHabitById(habit._id.toHexString())
            advanceUntilIdle()

            val latestState = viewModel.viewState.first()

            latestState.viewSate shouldBe ViewStatus.SUCCESS
            latestState.habits.firstOrNull() shouldBe habit
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelFreeSpecTest : FreeSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    "AddEditViewModel" - {
        "Fetching Habit By ID" - {
            "should fetch habit successfully" {
                runTest {
                    val habit = exerciseHabit
                    fakeRepository.addHabit(habit)

                    viewModel.getHabitById(habit._id.toHexString())
                    advanceUntilIdle()

                    val latestState = viewModel.viewState.first()
                    latestState.viewSate shouldBe ViewStatus.SUCCESS
                    latestState.habits.first() shouldBe habit
                }
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelExpectSpecTest : ExpectSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    context("Fetching Habit By ID") {
        expect("it should return habit successfully") {
            runTest {
                val habit = exerciseHabit
                fakeRepository.addHabit(habit)

                viewModel.getHabitById(habit._id.toHexString())
                advanceUntilIdle()

                val latestState = viewModel.viewState.first()
                latestState.viewSate shouldBe ViewStatus.SUCCESS
                latestState.habits.first() shouldBe habit
            }
        }
    }
})

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelFeatureSpecTest : FeatureSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var fakeRepository: FakeMongoRepository
    lateinit var viewModel: HabitDetailViewModel

    beforeEach {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMongoRepository()
        fakeRepository.shouldFail = false
        fakeRepository.habitList.clear()
        viewModel = HabitDetailViewModel(fakeRepository)
    }

    afterEach {
        Dispatchers.resetMain()
        fakeRepository.habitList.clear()
        fakeRepository.shouldFail = false
    }

    feature("Fetching Habit By ID") {
        scenario("Should return habit successfully") {
            runTest {
                val habit = exerciseHabit
                fakeRepository.addHabit(habit)

                viewModel.getHabitById(habit._id.toHexString())
                advanceUntilIdle()

                val latestState = viewModel.viewState.first()
                latestState.viewSate shouldBe ViewStatus.SUCCESS
                latestState.habits.first() shouldBe habit
            }
        }
    }
})


