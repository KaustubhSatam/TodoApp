package com.kau.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kau.todoapp.ui.theme.TodoAppTheme
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextDecoration


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            TodoRoute(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                        composable("add") {
                            val parentEntry = remember {
                                navController.getBackStackEntry("home")
                            }
                            val viewModel: TodoViewModel = viewModel(parentEntry)
                            AddTaskScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("edit/{taskId}") { backStackEntry ->
                            val parentEntry = remember {
                                navController.getBackStackEntry("home")
                            }
                            val viewModel: TodoViewModel = viewModel(parentEntry)

                            val taskId =
                                backStackEntry.arguments?.getString("taskId")?.toIntOrNull()

                            EditTaskScreen(
                                navController = navController,
                                viewModel = viewModel,
                                taskId = taskId
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun TodoRoute(
    modifier: Modifier,
    navController: NavController,
    viewModel: TodoViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    TodoScreen(
        modifier,
        navController,
        tasks,
        viewModel::deleteTask,
        viewModel::clearAllTasks,
        viewModel::toggleTaskCompletion
    )
}

@Composable
fun TodoScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    tasks: List<Task>,
    onDeleteTask: (Int) -> Unit,
    onClearTask: () -> Unit,
    onToggleTaskCompletion: (Int) -> Unit
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
            Button(
                onClick = onClearTask,
                enabled = tasks.isNotEmpty()
            ) {
                Text("Clear All")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("add") {
                    launchSingleTop = true
                }
            }
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
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { _ -> onToggleTaskCompletion(task.id) })
                        Text(
                            task.title, modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate("edit/${task.id}")
                                }, style = MaterialTheme.typography.bodyLarge.copy(
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (task.isCompleted) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )

                        Button(onClick = { onDeleteTask(task.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: TodoViewModel
) {
    val text by viewModel.text.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Title
        Text(
            text = "Add Task",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field
        TextField(
            value = text,
            onValueChange = { viewModel.onTextChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter task") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.addTask()
                    keyboardController?.hide()
                    navController.popBackStack()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.addTask()
                navController.popBackStack()
            },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Task")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cancel Button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
fun EditTaskScreen(navController: NavHostController, viewModel: TodoViewModel, taskId: Int?) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val task = taskId?.let { viewModel.getTaskById(it) }
    var text by remember { mutableStateOf(task?.title ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Task",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Edit task") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (taskId != null) {
                        viewModel.updateTask(taskId, text)
                    }
                    keyboardController?.hide()
                    navController.popBackStack()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                if (taskId != null) {
                    viewModel.updateTask(taskId, text)
                }
                navController.popBackStack()
            },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Task")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cancel Button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    TodoAppTheme {
        TodoScreen(
            navController = rememberNavController(),
            tasks = listOf(Task(1, "Sample Task")),
            onDeleteTask = {},
            onClearTask = {},
            onToggleTaskCompletion = {}
        )
    }
}