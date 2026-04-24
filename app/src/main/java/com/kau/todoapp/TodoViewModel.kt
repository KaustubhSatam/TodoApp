package com.kau.todoapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class TodoViewModel : ViewModel() {
    var text by mutableStateOf("")
        private set

    var tasks = mutableStateListOf<String>()
        private set

    fun onTextChange(newText: String) {
        text = newText
    }

    fun addTask() {
        if (text.isNotEmpty()) {
            tasks.add(text)
            text = ""
        }
    }

    fun deleteTask(index: Int) {
        tasks.removeAt(index)
    }
}