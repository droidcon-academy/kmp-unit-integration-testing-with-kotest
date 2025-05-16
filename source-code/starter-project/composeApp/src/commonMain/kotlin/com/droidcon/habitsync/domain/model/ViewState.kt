package com.droidcon.habitsync.domain.model

import com.droidcon.habitsync.utils.ViewStatus

data class ViewState(
    val viewSate: ViewStatus = ViewStatus.INITIAL,
    val habits: List<Habit> = emptyList(),
    val message: String? = null
)
