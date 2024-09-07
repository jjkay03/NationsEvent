package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.features.*
import com.jjkay03.nationsevent.patches.*
import com.jjkay03.nationsevent.specific.ng3.*
import com.jjkay03.nationsevent.specific.ng3.commands.*
import com.jjkay03.nationsevent.utils.*
import org.bukkit.plugin.java.JavaPlugin

class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_STAFF: String = "nationsevent.staff"
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
        getCommand("voicechatperms")?.setExecutor(VoicechatPermsCommand())
        getCommand("voicechatperms")?.tabCompleter = VoicechatPermsCommand() // Tab completer
        getCommand("bypassviewdistance")?.setExecutor(BypassViewDistanceCommand())
        // NG4
        // NG4 - Commands here


        // Register events
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(PVPAlerts(), this)
        server.pluginManager.registerEvents(MeatPlayerDeath(), this)
        server.pluginManager.registerEvents(IronDoor(), this)


        // Initialize patches
        AntiBlockGlitching()
        AntiEnderPearl()
    }

    // Plugin shutdown logic
    override fun onDisable() { }
}
