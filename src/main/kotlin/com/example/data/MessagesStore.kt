package com.example.data

object MessagesStore : DataManager() {
    private val messages = mutableListOf<String>()

    fun getMessages(): List<String> {
        return messages.toList()
    }

    fun saveMessage(message: String) {
        messages.add(message)
    }
}