package com.example.todo.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.todo.data.Task
import com.example.todo.data.TaskWithCategories
import com.example.todo.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    categories: List<Category>,
    currentTask: TaskWithCategories,
    onSaveTask: (Task, List<Long>) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(currentTask?.task?.text ?: "")) }
    val selectedCategories = remember { mutableStateListOf<Long>()
        .apply {
            addAll(currentTask?.categories?.map { it.categoryId } ?: emptyList())
        }}

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit") })
        }
    ) { innerPadding ->
        Text(
            text = currentTask.task.text,
            style = MaterialTheme.typography.headlineMedium,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text("Categories", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.6F)
            ) {
                items(categories) { category ->
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selectedCategories.contains(category.categoryId),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedCategories.add(category.categoryId)
                                } else {
                                    selectedCategories.remove(category.categoryId)
                                }
                            },
                        )
                        Text(text = category.name)
                    }
                }
            }
            Button(
                onClick = {
                    val task = Task(
                        id = currentTask?.task?.id ?: 0,
                        text = text.text,
                        done = false
                    )
                    onSaveTask(task, selectedCategories)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save task")
            }
        }
    }
}

