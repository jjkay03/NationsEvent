package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.features.*
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
        // NG3
        getCommand("voicechatpermsprisoners")?.setExecutor(NG3_VoicechatPermsPrisonersCommand())
        getCommand("voicechatpermsprisoners")?.tabCompleter = NG3_VoicechatPermsPrisonersCommand() // Tab completer


        // Register events
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(PVPAlerts(), this)
        server.pluginManager.registerEvents(MilkTheGator(), this)
        server.pluginManager.registerEvents(MeatPlayerDeath(), this)
        // NG3
        server.pluginManager.registerEvents(NG3_SeasonSpecific(), this)
    }

    // Plugin shutdown logic
    override fun onDisable() { }
}
