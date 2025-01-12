package com.example.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.data.Task
import com.example.todo.data.TaskCategoryCrossRef
import com.example.todo.data.TaskWithCategories
import com.example.todo.data.ToDoDatabase
import com.example.todo.data.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ToDoDatabase.getInstance(application).ToDoDao()
    private val _taskWithCategories = MutableLiveData<TaskWithCategories?>()
    val taskWithCategories: LiveData<TaskWithCategories?> = _taskWithCategories
    private val _categories = MutableLiveData<List<Category>>(emptyList())
    val categories: LiveData<List<Category>> = _categories



    fun loadTaskDetails(id: Long) {
        viewModelScope.launch {
            val task = dao.getTaskWithCategories(id)
            _taskWithCategories.postValue(task)
        }
    }

    fun loadCategories(){
        viewModelScope.launch {
            val categories = dao.getAllCategories()
            _categories.postValue(categories)
        }
    }

    internal fun validate(task: Task): Boolean{
        if (task.text.isEmpty()) return false
        return true
    }

    internal fun updateTask(task: Task, categoryIds: List<Long>){
        viewModelScope.launch {
            val id = dao.insertTask(task)
            dao.deleteTaskCrossRefs(id)
            categoryIds.forEach { categoryId ->
                dao.insertTaskCategoryCrossRef(TaskCategoryCrossRef(id, categoryId))
            }
        }
    }

    internal suspend fun updateTaskSuspended(task: Task, categoryIds: List<Long>){
        val id = dao.insertTask(task)
        dao.deleteTaskCrossRefs(id)
        categoryIds.forEach { categoryId ->
            dao.insertTaskCategoryCrossRef(TaskCategoryCrossRef(id, categoryId))
        }
    }
}
