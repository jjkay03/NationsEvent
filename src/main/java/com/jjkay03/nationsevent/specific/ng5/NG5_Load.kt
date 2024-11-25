package com.jjkay03.nationsevent.specific.ng5

import com.jjkay03.nationsevent.specific.ng5.commands.*
import com.jjkay03.nationsevent.specific.ng5.hangman_gonkas.NG5_HangMan
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NG5_Load(private val plugin: JavaPlugin) {

    init {
        // Log season specific loading
        Bukkit.getConsoleSender().sendMessage("§e[NationsEvent] Loading season specific code: §6NG5")

        // Class variables
        val ng5ClericImmunity = NG5_ClericImmunity()

        // Register commands
        plugin.getCommand("bearrage")?.setExecutor(NG5_BearRage())
        plugin.getCommand("wolfrage")?.setExecutor(NG5_WolfRageCommand())
        plugin.getCommand("wolfrageall")?.setExecutor(NG5_WolfRageAllCommand())
        plugin.getCommand("globalblindness")?.setExecutor(NG5_GlobalBlindnessCommand())
        plugin.getCommand("globalblindness")?.tabCompleter = NG5_GlobalBlindnessCommand() // Tab completer
        plugin.getCommand("rollroles")?.setExecutor(NG5_RollRoles())
        plugin.getCommand("role")?.setExecutor(NG5_Role())
        plugin.getCommand("clericimmunity")?.setExecutor(ng5ClericImmunity)

        // Register events
        plugin.server.pluginManager.registerEvents(NG5_SeasonSpecific(), plugin)
        plugin.server.pluginManager.registerEvents(NG5_HangMan(), plugin)
        plugin.server.pluginManager.registerEvents(ng5ClericImmunity, plugin)
    }
}