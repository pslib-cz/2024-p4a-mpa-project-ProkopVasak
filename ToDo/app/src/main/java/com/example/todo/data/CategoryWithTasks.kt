package com.example.todo.data
import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

data class CategoryWithTasks(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id",
        associateBy = Junction(TaskCategoryCrossRef::class)
    )
    val tasks: List<Task>
)
