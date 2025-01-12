package com.example.todo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.lifecycleScope
import com.example.todo.data.Task
import com.example.todo.data.TaskWithCategories
import com.example.todo.data.ToDoDao
import com.example.todo.data.ToDoDatabase
import com.example.todo.data.Category
import com.example.todo.ui.theme.ToDoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var db: ToDoDatabase
    private lateinit var dao: ToDoDao
    private var _tasks = mutableStateOf<List<Task>>(emptyList())
    private var _selectedCategory = mutableStateOf<Category?>(null)
    private var _categories = mutableStateOf<List<Category>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ToDoDatabase.getInstance(this)
        dao = db.ToDoDao()

        enableEdgeToEdge()
        setContent {
            ToDoTheme() {
                var tasks by remember { _tasks }
                var selectedCategory by remember { _selectedCategory }
                var categories by remember { _categories }

                LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        categories = dao.getAllCategories()
                        tasks = dao.getAllTasks()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                startActivity(Intent(this@MainActivity, AddTaskActivity::class.java))
                            },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).zIndex(100.0f)
                        ){
                            Icon(imageVector = Icons.Default.Add, "Add new task")
                        }
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "TODO", style = MaterialTheme.typography.headlineLarge)
                                IconButton(
                                    onClick = {
                                        startActivity(Intent(this@MainActivity, ManageCategoriesActivity::class.java))
                                    },
                                    modifier = Modifier.padding(0.dp).background(Color.Transparent)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        modifier = Modifier.padding(0.dp).background(Color.Transparent)
                                    )
                                }
                            }
                            CategoryFilterDropdown(
                                categories = categories,
                                selectedCategory = selectedCategory,
                                onCategorySelected = { category ->
                                    selectedCategory = category
                                    lifecycleScope.launch {
                                        if(category == null) tasks = dao.getAllTasks()
                                        else tasks = dao.getTasksByCategory(category.categoryId)
                                    }
                                }
                            )
                            TaskList(
                                tasks = tasks,
                                modifier = Modifier.padding(innerPadding),
                                onTaskClick = { task ->
                                    startActivity(TaskDetailActivity.newIntent(this@MainActivity, task.id))
                                })
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val dao = db.ToDoDao()
            _categories.value = dao.getAllCategories()

            if(_selectedCategory.value == null) {
                _tasks.value = dao.getAllTasks()
            }
            else {
                _tasks.value = dao.getTasksByCategory(_selectedCategory.value!!.categoryId)
            }
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, modifier: Modifier = Modifier, onTaskClick: (task: Task) -> Unit) {
    LazyColumn {
        items(tasks) { task ->
            TaskCard(task, onClick = { onTaskClick(task) })
        }
    }
}

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxSize(),
        onClick = { onClick() }
    ) {
        Column {
            Text(
                text = task.text,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp,16.dp,16.dp,0.dp)
            )
        }
    }
}

@Composable
fun CategoryFilterDropdown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("All") }

    Box(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { expanded = true },
        ) {
            Text(selectedText)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    expanded = false
                    selectedText = "All"
                    onCategorySelected(null)
                }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        expanded = false
                        selectedText = category.name
                        onCategorySelected(category)
                    }
                )
            }
        }
    }
}

