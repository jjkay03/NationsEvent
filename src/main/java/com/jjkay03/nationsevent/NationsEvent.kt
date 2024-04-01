package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.utils.*
import org.bukkit.plugin.java.JavaPlugin

class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent
        val PERM_ADMIN: String = "nationsevent.admin"
        val PERM_STAFF: String = "nationsevent.staff"
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0
    }

    // Plugin startup logic
    override fun onEnable() {
        INSTANCE = this

        // Startup info
        logger.info("§aNationsEvent is running!")
        logger.info("§aPlugin version: ${description.version}")

        // Config stuff
        saveDefaultConfig() // Save the default configuration if it doesn't exist
        reloadConfig() // Reload the configuration

        // Register commands
        getCommand("joinvc")?.setExecutor(JoinvcCommand())
        getCommand("announcesession")?.setExecutor(AnnounceSessionCommand())
        getCommand("sessiontime")?.setExecutor(SessionTimeCommand())
        getCommand("pvptoggle")?.setExecutor(PVPToggleCommand())
        getCommand("pvptoggle")?.tabCompleter = PVPToggleCommand() // Tab completer
        getCommand("pvpalerts")?.setExecutor(PVPAlertsCommand())
        //plugin.getCommand("teammessage")?.setExecutor(TeamMessageCommand(plugin))
        //plugin.getCommand("teammessage")?.tabCompleter = TeamMessageCommand(plugin) // Tab completer

        // Register events
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(MilkTheGator(), this)
        server.pluginManager.registerEvents(TeamCore(), this)



    }

    // Plugin shutdown logic
    override fun onDisable() {

    }
}
