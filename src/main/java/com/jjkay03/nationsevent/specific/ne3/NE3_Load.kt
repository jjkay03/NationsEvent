package com.jjkay03.nationsevent.specific.ne3

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NE3_Load (private val plugin: JavaPlugin) {

    init {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6NE3")

        // Class variables
        val ne3SocialStatus = NE3_SocialStatus()

        // Register commands
        plugin.getCommand("socialstatus")?.apply { setExecutor(ne3SocialStatus); tabCompleter = ne3SocialStatus }

        // Register events
        // ...
    }
}