package com.example.todo.data

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val done: Boolean
)