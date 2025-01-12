package com.example.todo.data

import androidx.room.Entity

@Entity(primaryKeys = ["id", "categoryId"], tableName = "taskCategoryCrossRef")
data class TaskCategoryCrossRef(
    val id: Long,
    val categoryId: Long
)
