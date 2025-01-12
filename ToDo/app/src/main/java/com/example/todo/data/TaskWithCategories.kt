package com.example.todo.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithCategories(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId",
        associateBy = Junction(TaskCategoryCrossRef::class)
    )
    val categories: List<Category>
)
