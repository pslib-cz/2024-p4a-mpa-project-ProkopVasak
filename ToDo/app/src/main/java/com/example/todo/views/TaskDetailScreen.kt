package com.example.todo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.todo.data.*


@Composable
fun TaskDetailScreen(
    task: TaskWithCategories,
    onEditTaskClick: (TaskWithCategories) -> Unit,
    onDeleteTaskClick: (TaskWithCategories) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = task.task.text,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight(800))){
                    append("Categories: ")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(task.categories){ category ->
                Text(
                    text = category.name,
                    modifier = Modifier.background(MaterialTheme.colorScheme.inversePrimary, CircleShape).padding(10.dp)
                )
            }
            if (task.categories.isEmpty()){
                item {
                    Text("None")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onEditTaskClick(task) },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text(text = "Edit")
            }

            Button(
                onClick = { onDeleteTaskClick(task) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text(text = "Delete")
            }
        }
    }
}
