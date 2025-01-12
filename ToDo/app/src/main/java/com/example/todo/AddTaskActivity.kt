package com.example.todo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todo.ui.theme.ToDoTheme
import com.example.todo.viewmodels.AddTaskViewModel
import com.example.todo.views.AddTaskScreen

class AddTaskActivity : ComponentActivity() {

    private val viewModel: AddTaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToDoTheme() {
                val categories by viewModel.categories.collectAsState(initial = emptyList())
                AddTaskScreen(
                    categories = categories,
                    onSaveTask = { task, categoryIds ->
                        if (!viewModel.validate(task)){
                            Toast.makeText(this, "Invalid task", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            viewModel.addTask(task, categoryIds)
                            Toast.makeText(this, "task +", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                )
            }
        }
    }
}
