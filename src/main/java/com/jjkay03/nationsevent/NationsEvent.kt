package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.commands.playerscale.*
import com.jjkay03.nationsevent.commands.voting.*
import com.jjkay03.nationsevent.features.*
import com.jjkay03.nationsevent.patches.*
import com.jjkay03.nationsevent.specific.ng5.*
import com.jjkay03.nationsevent.specific.ng5.commands.*
import com.jjkay03.nationsevent.specific.ng5.hangman_gonkas.NG5_HangMan
import com.jjkay03.nationsevent.utils.*
import me.neznamy.tab.api.TabAPI
import me.neznamy.tab.api.nametag.NameTagManager
import me.neznamy.tab.api.tablist.HeaderFooterManager
import org.bukkit.plugin.java.JavaPlugin

class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent
        const val PERM_ADMIN: String = "nationsevent.admin"
        const val PERM_PROD: String = "nationsevent.production"
        const val PERM_STAFF: String = "nationsevent.staff"
        const val PERM_SPECTATOR: String = "nationsevent.spectator"
        var SESSION_STARTED: Boolean = false
        var SESSION_START_TIME: Long = 0

        // TAB API
        lateinit var TAB_INSTANCE: TabAPI
        lateinit var TAB_NAMETAG_MANAGER: NameTagManager
        lateinit var TAB_HEADER_FOOTER_MANAGER: HeaderFooterManager
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
        TAB_HEADER_FOOTER_MANAGER = TAB_INSTANCE.headerFooterManager!!

        // Class variable
        val hideStaffCommand = HideStaffCommand()
        val ng5ClericImmunity = NG5_ClericImmunity()

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
        getCommand("hidestaff")?.setExecutor(hideStaffCommand)
        getCommand("hidestaff")?.tabCompleter = hideStaffCommand
        getCommand("fullmoon")?.setExecutor(FullMoonCommand())
        // NG5
        getCommand("bearrage")?.setExecutor(NG5_BearRage())
        getCommand("wolfrage")?.setExecutor(NG5_WolfRageCommand())
        getCommand("wolfrageall")?.setExecutor(NG5_WolfRageAllCommand())
        getCommand("globalblindness")?.setExecutor(NG5_GlobalBlindnessCommand())
        getCommand("globalblindness")?.tabCompleter = NG5_GlobalBlindnessCommand() // Tab completer
        getCommand("rollroles")?.setExecutor(NG5_RollRoles())
        getCommand("role")?.setExecutor(NG5_Role())
        getCommand("clericimmunity")?.setExecutor(ng5ClericImmunity)

        // Register events
        server.pluginManager.registerEvents(hideStaffCommand, this)
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(PVPAlerts(), this)
        server.pluginManager.registerEvents(MeatPlayerDeath(), this)
        server.pluginManager.registerEvents(IronDoor(), this)
        server.pluginManager.registerEvents(FarmProtection(), this)
        server.pluginManager.registerEvents(FreezeAll(), this)
        server.pluginManager.registerEvents(SpeedyBlocks(), this)
        // NG5
        server.pluginManager.registerEvents(NG5_SeasonSpecific(), this)
        server.pluginManager.registerEvents(NG5_HangMan(), this)
        server.pluginManager.registerEvents(ng5ClericImmunity, this)

        // Initialize patches
        AntiBlockGlitching()
        AntiEnderPearl()
    }

    // Plugin shutdown logic
    override fun onDisable() { }
}
