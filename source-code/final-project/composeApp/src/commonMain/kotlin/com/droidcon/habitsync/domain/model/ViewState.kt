package com.droidcon.habitsync.domain.model

import com.droidcon.habitsync.utils.ViewStatus

data class ViewState(
    val viewState: ViewStatus = ViewStatus.INITIAL,
    val habits: List<Habit> = emptyList(),
    val message: String? = null
)
