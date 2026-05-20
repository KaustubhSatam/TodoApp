package com.kau.todoapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class TodoViewModel : ViewModel() {
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    val filteredTasks = _tasks.value.filter {
        it.title.contains(_searchQuery.value, ignoreCase = true)
    }

    fun onTextChange(newText: String) {
        _text.value = newText
    }

    fun addTask() {
        val currentText = _text.value
        if (currentText.isBlank()) return

        val newTask = Task(
            title = currentText
        )

        _tasks.value += newTask
        clearText()
    }

    fun deleteTask(id: Int) {
        _tasks.value = _tasks.value.filter { it.id != id }
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


    fun clearText() {
        _text.value = ""
    }
}