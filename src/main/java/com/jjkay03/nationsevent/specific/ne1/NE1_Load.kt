package com.jjkay03.nationsevent.specific.ne1

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NE1_Load (private val plugin: JavaPlugin) {

    init {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6NE1")

        // Class variables
        val ne1PlayerScanner = NE1_PlayerScanner()
        val ne1PlayerTracker = NE1_PlayerTracker()
        val ne1ReducedHealth = NE1_ReducedHealth()

        // Register commands
        plugin.getCommand("giveplayerscanner")?.setExecutor(ne1PlayerScanner)
        plugin.getCommand("giveplayertracker")?.setExecutor(ne1PlayerTracker)
        plugin.getCommand("togglereducedhealth")?.setExecutor(ne1ReducedHealth)

        // Register events
        plugin.server.pluginManager.registerEvents(ne1PlayerScanner, plugin)
        plugin.server.pluginManager.registerEvents(ne1PlayerTracker, plugin)
        plugin.server.pluginManager.registerEvents(NE1_PlayersNoNetherite(), plugin)
        plugin.server.pluginManager.registerEvents(ne1ReducedHealth, plugin)
    }

}