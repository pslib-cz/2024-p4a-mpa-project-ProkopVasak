package com.example.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.data.*
import kotlinx.coroutines.launch

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ToDoDatabase.getInstance(application).ToDoDao()
    private val _taskWithCategories = MutableLiveData<TaskWithCategories?>()
    val taskWithCategories: LiveData<TaskWithCategories?> = _taskWithCategories

    fun loadTaskDetails(id: Long) {
        viewModelScope.launch {
            val task = dao.getTaskWithCategories(id)
            _taskWithCategories.postValue(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }
}