package com.jjkay03.nationsevent.specific_event

import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SpecificEvent (private val plugin: JavaPlugin) {

    companion object {
        const val SPECIFIC_EVENT_CODENAME: String = "NE4"
        var SPECIFIC_EVENT_LOADED: Boolean = false
    }

    init {
        // Load specific event code if event codename in config = SPECIFIC_EVENT_CODENAME
        if (Saves.EVENT_CODENAME == SPECIFIC_EVENT_CODENAME) load()
    }

    fun load() {
        SPECIFIC_EVENT_LOADED = true

        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6${SPECIFIC_EVENT_CODENAME}")

        // Class variables
        // ...

        // Register commands
        // ...

        // Register events
        // ...
    }
}