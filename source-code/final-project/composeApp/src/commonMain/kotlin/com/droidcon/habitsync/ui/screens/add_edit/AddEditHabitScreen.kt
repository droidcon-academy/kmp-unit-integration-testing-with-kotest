package com.droidcon.habitsync.ui.screens.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.droidcon.habitsync.domain.model.Habit
import com.droidcon.habitsync.utils.categories
import com.droidcon.habitsync.utils.frequencies

@Composable
fun AddEditHabitScreen(
    habit: Habit?,
    onSave: (Habit) -> Unit,
    onUpdate: (habit: Habit, habitId: String) -> Unit,
    onCancel: () -> Unit
) {
    var habitName by remember(habit?.name) { mutableStateOf(habit?.name ?: "") }
    var habitDescription by remember(habit?.description) {
        mutableStateOf(
            habit?.description ?: ""
        )
    }
    var selectedCategory by remember(habit?.category) {
        mutableStateOf(
            habit?.category ?: categories.first()
        )
    }
    var selectedFrequency by remember(habit?.frequency) {
        mutableStateOf(
            habit?.frequency ?: frequencies.first()
        )
    }
    var completedStreak by remember(habit?.completedStreak?.toString()) {
        mutableStateOf(habit?.completedStreak?.toString() ?: "0")
    }
    var totalStreak by remember(habit?.totalStreak?.toString()) {
        mutableStateOf(habit?.totalStreak?.toString() ?: "7")
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (habit == null) "Add Habit" else "Edit Habit") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = habitName,
                onValueChange = { habitName = it },
                label = { Text("Habit Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = habitDescription,
                onValueChange = { habitDescription = it },
                label = { Text("Habit Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
            )

            DropdownSelector(
                label = "Category",
                options = categories,
                selectedOption = selectedCategory,
                onOptionSelected = { selectedCategory = it }
            )

            DropdownSelector(
                label = "Frequency",
                options = listOf("Daily", "Weekly"),
                selectedOption = selectedFrequency,
                onOptionSelected = { selectedFrequency = it }
            )

            OutlinedTextField(
                value = completedStreak,
                onValueChange = {
                    completedStreak = it.filter { char -> char.isDigit() }
                },
                label = { Text("Completed Streaks") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = totalStreak,
                onValueChange = {
                    totalStreak = it.filter { char -> char.isDigit() }
                },
                label = { Text("Streak Length (Days or Weeks)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(contentColor = Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (habit == null) {
                    Button(
                        onClick = {
                            val newHabit = Habit().apply {
                                this.name = habitName
                                this.description = habitDescription
                                this.frequency = selectedFrequency
                                this.category = selectedCategory
                                this.totalStreak = totalStreak.toIntOrNull() ?: 0
                                this.completedStreak = completedStreak.toIntOrNull() ?: 0
                                this.completed = false
                            }
                            onSave(newHabit)
                        },
                        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Habit", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                if (habit != null) {
                    Button(
                        onClick = {
                            val updatedHabit = Habit().apply {
                                this.name = habitName
                                this.description = habitDescription
                                this.frequency = selectedFrequency
                                this.category = selectedCategory
                                this.totalStreak = totalStreak.toIntOrNull() ?: 0
                                this.completedStreak = completedStreak.toIntOrNull() ?: 0
                                this.completed = habit.completed
                            }
                            onUpdate(updatedHabit, habit._id.toHexString())
                        },
                        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.secondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Update Habit", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.body2)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        content = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}