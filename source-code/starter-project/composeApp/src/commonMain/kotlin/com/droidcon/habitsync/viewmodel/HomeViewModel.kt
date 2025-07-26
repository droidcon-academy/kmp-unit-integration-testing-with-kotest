package com.droidcon.habitsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.habitsync.domain.MongoRepository
import com.droidcon.habitsync.domain.model.ViewState
import com.droidcon.habitsync.ui.screens.home.FilterType
import com.droidcon.habitsync.ui.screens.home.SortType
import com.droidcon.habitsync.utils.Constant
import com.droidcon.habitsync.utils.ViewStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel(
    private val mongoRepository: MongoRepository
) : ViewModel() {
    private var _habits = MutableStateFlow(ViewState())
    val habits = _habits.asStateFlow()

    init {
        getHabits()
    }

    fun getHabits() {
        viewModelScope.launch {
            val result = mongoRepository.getHabits().first()
            result.onSuccess { habitData ->
                _habits.update {
                    it.copy(
                        viewState = ViewStatus.SUCCESS,
                        habits = habitData
                    )
                }
            }

            result.onFailure { exception ->
                _habits.update {
                    it.copy(
                        viewState = ViewStatus.FAILED,
                        habits = emptyList(),
                        message = exception.message ?: "An error occurred"
                    )
                }
            }
        }
    }

    fun deleteHabit(habitId: String?) {
        if (habitId == null) return
        viewModelScope.launch {
            mongoRepository.deleteHabit(habitId)
            getHabits()
        }
    }

    fun getFilteredHabits(filterType: FilterType) {
        val filter = when(filterType){
            FilterType.ALL -> Constant.ALL
            FilterType.DAILY -> Constant.DAILY
            FilterType.WEEKLY -> Constant.WEEKLY
        }
        viewModelScope.launch {
            val result = mongoRepository.getFilteredHabits(filter).first()
            result.onSuccess { habitData ->
                _habits.update {
                    it.copy(
                        viewState = ViewStatus.SUCCESS,
                        habits = habitData
                    )
                }
            }
            result.onFailure { exception ->
                _habits.update {
                    it.copy(
                        viewState = ViewStatus.FAILED,
                        habits = emptyList(),
                        message = exception.message ?: "An error occurred"
                    )
                }
            }
        }
    }

    fun sortHabits(sortType: SortType) {
        when (sortType) {
            SortType.ASCENDING -> {
                _habits.update {
                    it.copy(
                        viewState = ViewStatus.SUCCESS,
                        habits = _habits.value.habits.sortedBy { it.name }
                    )
                }
            }

            SortType.DESCENDING -> {
                _habits.update {
                    it.copy(
                        viewState = ViewStatus.SUCCESS,
                        habits = _habits.value.habits.sortedByDescending { it.name }
                    )
                }
            }
        }
    }
}
