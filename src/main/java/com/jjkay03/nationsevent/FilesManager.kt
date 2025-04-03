package com.jjkay03.nationsevent

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object FilesManager {

    // Function to create default directories
    fun createDefaultDirectories() {
        NationsEvent.INSTANCE.logger.info("Generating default plugin directories...")
        createDirectory(Saves.DIR_EVENT_IGNS)
        createDirectory(Saves.DIR_EXPORTED_VOTES)
    }

    // Function to create default files
    fun createDefaultFiles() {
        NationsEvent.INSTANCE.logger.info("Generating default plugin files...")
        createFile(NationsEvent.INSTANCE, Saves.FILE_WEBHOOKS, Saves.FILE_NAME_WEBHOOKS)
    }

    // Function to create a directory when it doesn't already exist
    fun createDirectory(directory: File) {
        if (!directory.exists()) directory.mkdirs()
    }

    // Function to create a file when it doesn't already exist
    fun createFile(plugin: JavaPlugin, file: File, resourcePath: String? = null) {
        if (file.exists()) return
        file.parentFile.mkdirs()
        if (resourcePath != null) plugin.saveResource(resourcePath, false)
        else file.createNewFile()
    }
}