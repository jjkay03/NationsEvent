package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.AnnounceSessionCommand
import com.jjkay03.nationsevent.commands.JoinvcCommand
import com.jjkay03.nationsevent.commands.TeamMessageCommand
import com.jjkay03.nationsevent.utils.MilkTheGator
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var instance: NationsEvent
        lateinit var logger: Logger
    }

    // Plugin startup logic
    override fun onEnable() {
        instance = this

        // Startup info
        logger.info("§aNationsEvent is running!")
        logger.info("§aPlugin version: ${description.version}")

        // Config stuff
        saveDefaultConfig() // Save the default configuration if it doesn't exist
        reloadConfig() // Reload the configuration

        // Get commands
        getCommand("joinvc")?.setExecutor(JoinvcCommand())
        getCommand("announcesession")?.setExecutor(AnnounceSessionCommand())
        getCommand("teammessage")?.setExecutor(TeamMessageCommand(this))
        getCommand("teammessage")?.tabCompleter = TeamMessageCommand(this) // Tab completer

        // Register event handler
        server.pluginManager.registerEvents(MilkTheGator(), this)
    }

    // Plugin shutdown logic
    override fun onDisable() {

    }
}
