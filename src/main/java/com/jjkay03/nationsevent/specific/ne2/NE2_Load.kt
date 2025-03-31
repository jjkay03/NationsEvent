package com.jjkay03.nationsevent.specific.ne2

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NE2_Load (private val plugin: JavaPlugin) {

    init {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6NE2")

        // Class variables
        //...

        // Register commands
        //...

        // Register events
        //...
    }
}