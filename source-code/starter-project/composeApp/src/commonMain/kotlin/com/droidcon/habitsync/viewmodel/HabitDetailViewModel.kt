package com.droidcon.habitsync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.habitsync.domain.MongoRepository
import com.droidcon.habitsync.domain.model.ViewState
import com.droidcon.habitsync.utils.ViewStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HabitDetailViewModel(
    private val mongoRepository: MongoRepository
): ViewModel() {
    private var _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    fun getHabitById(id: String?){
        if(id == null) return
        viewModelScope.launch {
            val result = mongoRepository.getHabitById(id).first()
            result.onSuccess { habitData ->
                _viewState.update {
                    it.copy(
                        viewState = ViewStatus.SUCCESS,
                        habits = habitData
                    )
                }
            }

            result.onFailure { exception ->
                _viewState.update {
                    it.copy(
                        viewState = ViewStatus.FAILED,
                        habits = emptyList(),
                        message =  exception.message ?: "An error occurred"
                    )
                }
            }
        }
    }
}
