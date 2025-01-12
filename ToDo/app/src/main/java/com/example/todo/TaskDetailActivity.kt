package com.example.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.todo.data.TaskWithCategories
import com.example.todo.ui.theme.ToDoTheme
import com.example.todo.viewmodels.TaskDetailViewModel
import com.example.todo.views.TaskDetailScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

class TaskDetailActivity : ComponentActivity() {

    private val viewModel: TaskDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra(TASK_ID_EXTRA, -1L)
        if (id == -1L) {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("TODO","Task id is ${id.toString()}")
        viewModel.loadTaskDetails(id)

        setContent {
            val task = viewModel.taskWithCategories.observeAsState()
            ToDoTheme() {
                Scaffold { padding ->
                    if (task.value != null) {
                        TaskDetailScreen(
                            task = task.value!!,
                            modifier = Modifier.padding(padding),
                            onDeleteTaskClick = { taskWithCategories -> deleteTaskAndFinish(taskWithCategories)},
                            onEditTaskClick = { taskWithCategories ->  editTask(taskWithCategories.task.id) }
                        )
                    }
                    else{
                        Text("Task not found")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getLongExtra(TASK_ID_EXTRA, -1L)
        if (id != -1L) {
            viewModel.loadTaskDetails(id)
        }
    }

    private fun deleteTaskAndFinish(task: TaskWithCategories) {
        lifecycleScope.launch {
            viewModel.deleteTask(task.task)
            Toast.makeText(this@TaskDetailActivity, "Task deleted", Toast.LENGTH_SHORT).show()
            finish() // Return to the previous screen
        }
    }

    private fun editTask(id: Long) {
        lifecycleScope.launch {
            startActivity(EditTaskActivity.newIntent(this@TaskDetailActivity, id))
        }
    }

    companion object {
        private const val TASK_ID_EXTRA = "task_id"

        fun newIntent(context: Context, id: Long): Intent {
            return Intent(context, TaskDetailActivity::class.java).apply {
                putExtra(TASK_ID_EXTRA, id)
            }
        }
    }
}

