package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.todo.data.*
import com.example.todo.ui.theme.ToDoTheme
import com.example.todo.views.ManageCategoriesScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageCategoriesActivity : ComponentActivity() {
    private lateinit var db: ToDoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ToDoDatabase.getInstance(this)
        enableEdgeToEdge()

        setContent {
            var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
            LaunchedEffect(Unit) {
                categories = loadCategories()
            }
            ToDoTheme() {
                ManageCategoriesScreen(
                    categories = categories,
                    onAddCategory = { name ->
                        lifecycleScope.launch {
                            if (name.isNotBlank()) {
                                addCategory(name)
                                categories = loadCategories()
                            }
                        }
                    },
                    onDeleteCategory = { category ->
                        lifecycleScope.launch {
                            deleteCategory(category)
                            categories = loadCategories()
                        }
                    }
                )
            }
        }
    }

    private suspend fun loadCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            db.ToDoDao().getAllCategories()
        }
    }

    private suspend fun addCategory(name: String) {
        withContext(Dispatchers.IO) {
            db.ToDoDao().insertCategory(Category(name = name))
        }
    }

    private suspend fun deleteCategory(category: Category) {
        withContext(Dispatchers.IO) {
            db.ToDoDao().deleteCategory(category)
        }
    }
}
