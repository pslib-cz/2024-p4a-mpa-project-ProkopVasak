package com.example.todo.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface ToDoDao{
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskCategoryCrossRef(crossRef: TaskCategoryCrossRef)
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskWithCategories(id: Long): TaskWithCategories
    @Transaction
    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryWithTasks(categoryId: Long): CategoryWithTasks
    @Delete
    suspend fun deleteTask(task: Task)
    @Delete
    suspend fun deleteCategory(category: Category)
    @Query("DELETE FROM taskcategorycrossref WHERE categoryId = :categoryId")
    suspend fun deleteCategoryCrossRefs(categoryId: Long)
    @Query("DELETE FROM taskCategoryCrossRef WHERE id = :id")
    suspend fun deleteTaskCrossRefs(id: Long)
    @Query("DELETE FROM taskcategorycrossref WHERE id = :id AND categoryId = :categoryId")
    suspend fun deleteTaskCategoryCrossRef(id: Long, categoryId: Long)

    @Transaction
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksWithCategories(): List<TaskWithCategories>

    @Transaction
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesWithTasks(): List<CategoryWithTasks>

    @Query("""
        SELECT * FROM tasks 
        INNER JOIN taskCategoryCrossRef 
        ON tasks.id = taskCategoryCrossRef.id 
        WHERE taskCategoryCrossRef.categoryId = :categoryId
    """)
    suspend fun getTasksByCategory(categoryId: Long): List<Task>
}

@Database(
    entities = [Task::class, Category::class, TaskCategoryCrossRef::class],
    version = 1,
)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun ToDoDao(): ToDoDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getInstance(context: Context): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                )
                    .addCallback(SeedDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class SeedDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Seed data asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                getInstance(context).ToDoDao().apply {
                    val domovId = insertCategory(Category(name = "Domov"))
                    val praceId = insertCategory(Category(name = "Práce"))

                    val task1Id = insertTask(Task(text = "Koupit pomeranč", done = false))
                    val task2Id = insertTask(Task(text = "Koupit barvu", done = false))

                    insertTaskCategoryCrossRef(
                        TaskCategoryCrossRef(id = task1Id, categoryId = domovId)
                    )
                    insertTaskCategoryCrossRef(
                        TaskCategoryCrossRef(id = task2Id, categoryId = praceId)
                    )
                }
            }
        }
    }
}