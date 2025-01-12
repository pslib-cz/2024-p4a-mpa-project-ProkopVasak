package com.example.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import com.example.todo.ui.theme.ToDoTheme
import com.example.todo.viewmodels.EditTaskViewModel
import com.example.todo.views.EditTaskScreen
import kotlinx.coroutines.runBlocking

class EditTaskActivity : ComponentActivity() {

    private val viewModel: EditTaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra(EditTaskActivity.TASK_ID_EXTRA, -1L)
        if (id == -1L) {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.loadTaskDetails(id)
        viewModel.loadCategories()

        setContent {
            val task = viewModel.taskWithCategories.observeAsState()
            val categories = viewModel.categories.observeAsState()
            ToDoTheme() {
                if (task.value == null || categories.value == null) {
                    Text("Loading...")
                } else {
                    EditTaskScreen(
                        currentTask = task.value!!,
                        categories = categories.value!!,
                        onSaveTask = { task, categoryIds ->
                            if (!viewModel.validate(task)) {
                                Toast.makeText(this, "Invalid task", Toast.LENGTH_SHORT).show()
                            } else {
                                runBlocking {
                                    viewModel.updateTaskSuspended(task, categoryIds)
                                }
                                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    )
                }
            }
        }

    }

    companion object {
        private const val TASK_ID_EXTRA = "task_id"

        fun newIntent(context: Context, id: Long): Intent {
            return Intent(context, EditTaskActivity::class.java).apply {
                putExtra(TASK_ID_EXTRA, id)
            }
        }
    }
}