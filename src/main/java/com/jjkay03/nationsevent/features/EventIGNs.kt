package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.FilesManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.io.FileWriter

class EventIGNs : Listener {
    private val fileName = "${Config.EVENT_CODENAME} IGNs.txt"

    // INITIALIZATION
    init {
        load(Config.FEATURES_EVENT_IGNS)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        // End if feature disabled
        if (!enabled) return

        // Create files
        FilesManager.createDirectory(Saves.DIR_EVENT_IGNS)

        // Register event
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading feature: ${this::class.simpleName}")
    }

    // Detect player join and save IGN directly
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        saveIGN(event.player.name)
    }

    // Synchronized helper function to save the player's IGN to a text file
    @Synchronized
    private fun saveIGN(playerName: String) {
        val file = File(Saves.DIR_EVENT_IGNS, fileName)

        // Check if the file exists; if not, create it with header and first IGN
        if (!file.exists()) {
            FileWriter(file).use { writer ->
                writer.write("IGNs of all players that joined the server during ${Config.EVENT_CODENAME} :\n\n")
                writer.write(playerName)  // Add the first IGN
            }
            return
        }

        // If file exists, load current content to check for duplicates
        val content = file.readText()
        if (content.contains(playerName)) return // IGN already exists, do nothing

        // Append new IGN to the file
        FileWriter(file, true).use { writer -> writer.write(", $playerName") }
        NationsEvent.INSTANCE.logger.info("Added $playerName to ${Config.EVENT_CODENAME} IGNs save file")
    }
}
