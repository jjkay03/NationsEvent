package com.jjkay03.nationsevent.specific_event

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.specific_event.ne3.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class LoadSpecificEvent (private val plugin: JavaPlugin) {

    companion object {
        const val SPECIFIC_EVENT_CODENAME: String = "NE3"
    }

    init {
        // Load specific event code if event codename in config = SPECIFIC_EVENT_CODENAME
        if (Saves.EVENT_CODENAME == SPECIFIC_EVENT_CODENAME) load()
    }

    fun load() {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6${SPECIFIC_EVENT_CODENAME}")

        // Class variables
        val ne3SocialStatus = SocialStatusCommand()

        // Register commands
        plugin.getCommand("socialstatus")?.apply { setExecutor(ne3SocialStatus); tabCompleter = ne3SocialStatus }

        // Register events
        // ...
    }
}