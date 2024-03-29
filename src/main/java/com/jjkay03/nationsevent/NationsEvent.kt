package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.utils.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent
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

        // Get commands
        getCommand("joinvc")?.setExecutor(JoinvcCommand())
        getCommand("announcesession")?.setExecutor(AnnounceSessionCommand())
        getCommand("sessiontime")?.setExecutor(SessionTimeCommand())
        getCommand("pvptoggle")?.setExecutor(PVPToggle())
        //getCommand("teammessage")?.setExecutor(TeamMessageCommand(this))
        //getCommand("teammessage")?.tabCompleter = TeamMessageCommand(this) // Tab completer

        // Register event handler
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(MilkTheGator(), this)
        server.pluginManager.registerEvents(TeamCore(), this)
    }

    // Plugin shutdown logic
    override fun onDisable() {

    }
}
