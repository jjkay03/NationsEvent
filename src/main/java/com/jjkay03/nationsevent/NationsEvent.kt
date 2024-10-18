package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.commands.playerscale.PlayerScaleCommand
import com.jjkay03.nationsevent.commands.playerscale.PlayerScaleRestAllCommand
import com.jjkay03.nationsevent.commands.voting.*
import com.jjkay03.nationsevent.features.*
import com.jjkay03.nationsevent.patches.*
import com.jjkay03.nationsevent.specific.ng4.NG5_SeasonSpecific
import com.jjkay03.nationsevent.specific.ng4.commands.*
import com.jjkay03.nationsevent.utils.*
import me.neznamy.tab.api.TabAPI
import me.neznamy.tab.api.nametag.NameTagManager
import org.bukkit.plugin.java.JavaPlugin

class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_PROD: String = "nationsevent.production"
        const val PERM_STAFF: String = "nationsevent.staff"
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0

        // TAB API
        lateinit var TAB_INSTANCE: TabAPI
        lateinit var TAB_NAMETAG_MANAGER: NameTagManager
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

        // TAB API
        TAB_INSTANCE = TabAPI.getInstance()
        TAB_NAMETAG_MANAGER = TAB_INSTANCE.nameTagManager!!


        // Register commands
        getCommand("joinvc")?.setExecutor(JoinvcCommand())
        getCommand("joinstage")?.setExecutor(JoinStageCommand())
        getCommand("announcesession")?.setExecutor(AnnounceSessionCommand())
        getCommand("sessiontime")?.setExecutor(SessionTimeCommand())
        getCommand("pvptoggle")?.setExecutor(PVPToggleCommand())
        getCommand("pvptoggle")?.tabCompleter = PVPToggleCommand() // Tab completer
        getCommand("pvpalerts")?.setExecutor(PVPAlertsCommand())
        getCommand("voicechatperms")?.setExecutor(VoicechatPermsCommand())
        getCommand("voicechatperms")?.tabCompleter = VoicechatPermsCommand() // Tab completer
        getCommand("bypassviewdistance")?.setExecutor(BypassViewDistanceCommand())
        getCommand("vote")?.setExecutor(VoteCommand())
        getCommand("topvotes")?.setExecutor(TopVotesCommand())
        getCommand("clearvotes")?.setExecutor(ClearVotesCommand())
        getCommand("exportvotes")?.setExecutor(ExportVotesCommand())
        getCommand("lockvotes")?.setExecutor(LockVotesCommand())
        getCommand("playerscale")?.setExecutor(PlayerScaleCommand())
        getCommand("playerscalerestall")?.setExecutor(PlayerScaleRestAllCommand())
        getCommand("needadmin")?.setExecutor(NeedAdminCommand())
        getCommand("needadmin")?.tabCompleter = NeedAdminCommand()
        getCommand("freezeall")?.setExecutor(FreezeAllCommand())
        // NG4
        getCommand("wolfrage")?.setExecutor(NG5_WolfRageCommand())
        getCommand("wolfrageall")?.setExecutor(NG5_WolfRageAllCommand())
        getCommand("globalblindness")?.setExecutor(NG5_GlobalBlindnessCommand())
        getCommand("globalblindness")?.tabCompleter = NG5_GlobalBlindnessCommand() // Tab completer
        getCommand("rollroles")?.setExecutor(NG5_RollRoles())
        getCommand("role")?.setExecutor(NG5_Role())


        // Register events
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(PVPAlerts(), this)
        server.pluginManager.registerEvents(MeatPlayerDeath(), this)
        server.pluginManager.registerEvents(IronDoor(), this)
        server.pluginManager.registerEvents(FarmProtection(), this)
        server.pluginManager.registerEvents(FreezeAll(), this)
        // NG4
        server.pluginManager.registerEvents(NG5_SeasonSpecific(), this)


        // Initialize patches
        AntiBlockGlitching()
        AntiEnderPearl()
    }

    // Plugin shutdown logic
    override fun onDisable() { }
}
