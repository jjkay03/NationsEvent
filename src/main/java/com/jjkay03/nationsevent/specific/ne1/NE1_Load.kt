package com.jjkay03.nationsevent.specific.ne1

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NE1_Load (private val plugin: JavaPlugin) {

    init {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6NE1")

        // Register commands
        plugin.getCommand("giveplayerscanner")?.setExecutor(NE1_PlayerScanner())

        // Register events
        plugin.server.pluginManager.registerEvents(NE1_PlayerScanner(), plugin)
        plugin.server.pluginManager.registerEvents(NE1_PlayersNoNetherite(), plugin)

    }

}