package com.jjkay03.nationsevent.specific.ng6

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NG6_Load (private val plugin: JavaPlugin) {

    init {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6NG6")

        // Class variables
        // ...

        // Register commands
        // ...

        // Register events
        // ...
    }
}