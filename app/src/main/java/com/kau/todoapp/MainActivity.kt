package com.kau.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kau.todoapp.ui.theme.TodoAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*   Greeting(
                           name = "Android",
                           modifier = Modifier.padding(innerPadding)
                       )*/
                    TodoRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TodoRoute(modifier: Modifier, viewModel: TodoViewModel = viewModel()) {
    val text by viewModel.text.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    TodoScreen(
        modifier,
        text,
        tasks,
        viewModel::onTextChange,
        viewModel::addTask,
        viewModel::deleteTask,
        viewModel::clearAllTask
    )
}

@Composable
fun TodoScreen(
    modifier: Modifier = Modifier,
    text: String,
    tasks: List<Task>,
    onTextChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (Int) -> Unit,
    onClearTask: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "My Todos (${tasks.size})",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onClearTask) { Text("Clear All") }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Task") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onAddTask()
                    keyboardController?.hide()
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAddTask,
            enabled = text.isNotBlank()
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No tasks yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = tasks,
                    key = { task -> task.id }
                ) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, modifier = Modifier.weight(1f))

                        Button(onClick = { onDeleteTask(task.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoAppTheme {
        TodoScreen(
            text = "",
            tasks = listOf(Task(1, "Sample Task")),
            onTextChange = {},
            onAddTask = {},
            onDeleteTask = {},
            onClearTask = {}
        )
    }
}