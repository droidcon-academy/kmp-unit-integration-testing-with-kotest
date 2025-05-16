package com.droidcon.habitsync.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidcon.habitsync.domain.model.Habit

enum class SortType { ASCENDING, DESCENDING }
enum class FilterType { ALL, DAILY, WEEKLY }

@Composable
fun HomeScreen(
    habits: List<Habit>,
    onClickDetailHabit: (String) -> Unit,
    onClickEditHabit: (String?) -> Unit,
    onClickAddHabit: (String?) -> Unit,
    onClickDeleteHabit: (String?) -> Unit,
    onClickFilter: (filterType: FilterType) -> Unit,
    onClickSort: (SortType) -> Unit,
    onClickMotivation: () -> Unit
) {
    var sortType by remember { mutableStateOf(SortType.ASCENDING) }
    var filterType by remember { mutableStateOf(FilterType.ALL) }

    var showSortDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habits") },
                actions = {
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onClickAddHabit(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }

    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn {
                items(habits.size) { habit ->
                    HabitItem(
                        habit = habits[habit],
                        onClickDetailHabit = { habitId ->
                            onClickDetailHabit(habitId)
                        },
                        onClickEditHabit = { habitId ->
                            onClickEditHabit(habitId)
                        },
                        onClickDeleteHabit = { habitId ->
                            onClickDeleteHabit(habitId)
                        }
                    )
                }
            }
            Button(
                onClick = { onClickMotivation() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF6200EA)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(8.dp)
            ) {
                Icon(Icons.Filled.Bolt, contentDescription = "Motivation", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get Motivated!", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSortDialog) {
        SortDialog(
            onSortSelected = { selectedSort ->
                sortType = selectedSort
                onClickSort(selectedSort)
            },
            onDismiss = { showSortDialog = false }
        )
    }

    if (showFilterDialog) {
        FilterDialog(
            onFilterSelected = { selectedFilter ->
                filterType = selectedFilter
                onClickFilter(selectedFilter)
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onClickDetailHabit: (String) -> Unit,
    onClickEditHabit: (String?) -> Unit,
    onClickDeleteHabit: (String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClickDetailHabit(habit._id.toHexString()) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = habit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                val progress = habit.completedStreak.toFloat() / habit.totalStreak.toFloat()
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color(0xFF00C4B4)
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "${habit.completedStreak} / ${habit.totalStreak} days",
                    style = MaterialTheme.typography.body2
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (habit.completed) Color.Green.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (habit.completed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "Completion Icon",
                        tint = if (habit.completed) Color(0xFF2E7D32) else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (habit.completed) "Completed" else "Not Completed",
                        style = MaterialTheme.typography.body2.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (habit.completed)  Color(0xFF2E7D32)  else Color.Red
                        )
                    )
                }

            }

            Row {
                IconButton(onClick = { onClickEditHabit(habit._id.toHexString()) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Habit", tint = Color.Blue)
                }
                IconButton(onClick = { onClickDeleteHabit(habit._id.toHexString()) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Habit",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun SortDialog(onSortSelected: (SortType) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort Habits") },
        text = { Text("Choose how you want to sort your habits.") },
        confirmButton = {
            Column {
                TextButton(onClick = { onSortSelected(SortType.ASCENDING); onDismiss() }) {
                    Text("A → Z (Ascending)")
                }
                TextButton(onClick = { onSortSelected(SortType.DESCENDING); onDismiss() }) {
                    Text("Z → A (Descending)")
                }
            }
        }
    )
}

@Composable
fun FilterDialog(onFilterSelected: (FilterType) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Habits") },
        text = { Text("Choose a filter option for your habits.") },
        confirmButton = {
            Column {
                TextButton(onClick = { onFilterSelected(FilterType.ALL); onDismiss() }) {
                    Text("All")
                }
                TextButton(onClick = { onFilterSelected(FilterType.DAILY); onDismiss() }) {
                    Text("Daily")
                }
                TextButton(onClick = { onFilterSelected(FilterType.WEEKLY); onDismiss() }) {
                    Text("Weekly")
                }
            }
        }
    )
}