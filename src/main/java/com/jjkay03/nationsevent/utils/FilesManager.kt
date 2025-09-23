package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object FilesManager {

    // Function to create default directories and files
    fun createDefaults() {
        // Default directories
        NationsEvent.INSTANCE.logger.info("Generating default plugin directories...")

        // Default files
        NationsEvent.INSTANCE.logger.info("Generating default plugin files...")
        createPluginEmbeddedFile(NationsEvent.INSTANCE, Saves.FILE_WEBHOOKS, Saves.FILE_NAME_WEBHOOKS)
    }

    // Function to create a directory when it doesn't already exist
    fun createDirectory(directory: File) {
        if (!directory.exists()) directory.mkdirs()
    }

    // Function to create a file when it doesn't already exist
    fun createFile(file: File) {
        if (file.exists()) return
        file.parentFile.mkdirs()
        file.createNewFile()
    }

    // Function to create plugin embedded file (file that in "resources" dir of plugin)
    fun createPluginEmbeddedFile(plugin: JavaPlugin, file: File, resourcePath: String? = null) {
        if (file.exists()) return
        file.parentFile.mkdirs()
        if (resourcePath != null) plugin.saveResource(resourcePath, false)
        else file.createNewFile()
    }
}