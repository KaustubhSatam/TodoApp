package com.kau.todoapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class TodoViewModel : ViewModel() {
    private var nextId = 0
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    fun onTextChange(newText: String) {
        _text.value = newText
    }

    fun addTask() {
        val currentText = _text.value
        if (currentText.isBlank()) return

        val newTask = Task(
            id = nextId++,
            title = currentText
        )

        _tasks.value = _tasks.value + newTask
        _text.value = ""
    }

    fun deleteTask(id: Int) {
        _tasks.value = tasks.value.filter { it.id != id }
    }

    fun clearAllTasks() {
        _tasks.value = emptyList()
    }

    fun getTaskById(id: Int): Task? {
        return _tasks.value.find { it.id == id }
    }

    fun updateTask(id: Int, newTitle: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) it.copy(title = newTitle)
            else it
        }
    }

    fun toggleTaskCompletion(id: Int) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) {
                it.copy(isCompleted = !it.isCompleted)
            } else it
        }
    }
}