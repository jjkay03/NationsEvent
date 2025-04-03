package com.jjkay03.nationsevent.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LogsManager {

    // Function to log text to log file
    fun log(file: File, logType: String, message: String) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val timestamp = LocalDateTime.now().format(timeFormatter)
        val logMessage = "[$timestamp] [$logType] : $message\n"
        file.appendText(logMessage)
    }

    // Function that generates correct formatting for log file name
    fun generateLogFileName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-H-H")
        val timestamp = LocalDateTime.now().format(formatter)
        return "$timestamp.log"
    }
}