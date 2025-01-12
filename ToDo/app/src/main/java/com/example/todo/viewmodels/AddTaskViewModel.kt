package com.example.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.Task
import com.example.todo.data.TaskCategoryCrossRef
import com.example.todo.data.ToDoDatabase
import com.example.todo.data.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ToDoDatabase.getInstance(application).ToDoDao()
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        viewModelScope.launch {
            _categories.value = dao.getAllCategories()
        }
    }

    internal fun getAllCategories(): List<Category> {
        var categories = emptyList<Category>()
        viewModelScope.launch {
            categories = dao.getAllCategories()
        }
        return categories
    }

    internal fun validate(task: Task): Boolean{
        if (task.text.isEmpty()) return false
        return true
    }

    internal fun addTask(task: Task, categoryIds: List<Long>){
        viewModelScope.launch {
            val id = dao.insertTask(task)
            categoryIds.forEach { categoryId ->
                dao.insertTaskCategoryCrossRef(TaskCategoryCrossRef(id, categoryId))
            }
        }
    }
}