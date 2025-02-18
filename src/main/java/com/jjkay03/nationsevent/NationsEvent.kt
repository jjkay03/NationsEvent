package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.*
import com.jjkay03.nationsevent.commands.playerscale.*
import com.jjkay03.nationsevent.commands.voting.*
import com.jjkay03.nationsevent.features.*
import com.jjkay03.nationsevent.gameplay.*
import com.jjkay03.nationsevent.group_chat.GroupChatsCommands
import com.jjkay03.nationsevent.patches.*
import com.jjkay03.nationsevent.server_settings.*
import com.jjkay03.nationsevent.specific.ne1.*
import com.jjkay03.nationsevent.utils.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import me.neznamy.tab.api.TabAPI
import me.neznamy.tab.api.nametag.NameTagManager
import me.neznamy.tab.api.tablist.HeaderFooterManager
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.GroupManager
import org.ipvp.canvas.MenuFunctionListener

open class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent
        // TAB API
        lateinit var TAB_INSTANCE: TabAPI
        lateinit var TAB_NAMETAG_MANAGER: NameTagManager
        lateinit var TAB_HEADER_FOOTER_MANAGER: HeaderFooterManager
        // LUCKPERMS API
        lateinit var LP_INSTANCE: LuckPerms
        lateinit var LP_GROUP_MANAGER: GroupManager
    }

    // Plugin startup logic
    override fun onEnable() {
        INSTANCE = this

        // Startup info
        Utils.displayPluginWelcomeMessage("§e")
        logger.info("NationsEvent is running!")
        logger.info("Plugin version: ${description.version}")

        // Config stuff
        saveDefaultConfig() // Save the default configuration if it doesn't exist
        reloadConfig() // Reload the configuration

        getAPIs() // Get all APIs instances and info
        Saves() // Load all variables in saves class

        // Class variable
        val hideStaffCommand = HideStaffCommand()

        // Register commands
        GroupChatsCommands.registerGroupChatCommands(this)
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
        getCommand("permanentmessage")?.setExecutor(PermanentMessageCommand())
        getCommand("admingui")?.setExecutor(AdminGUICommand())
        getCommand("globalchat")?.setExecutor(GlobalChatCommand())
        getCommand("globalchat")?.tabCompleter = GlobalChatCommand()
        getCommand("applyserverpack")?.setExecutor(ApplyServerPackCommand())
        getCommand("restockvillagers")?.setExecutor(RestockVillagers())

        // Register events
        Bukkit.getPluginManager().registerEvents(MenuFunctionListener(), this) // Canvas MenuFunctionListener
        server.pluginManager.registerEvents(hideStaffCommand, this)
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(PVPAlerts(), this)
        server.pluginManager.registerEvents(MeatPlayerDeath(), this)
        server.pluginManager.registerEvents(IronDoor(), this)
        server.pluginManager.registerEvents(FarmProtection(), this)
        server.pluginManager.registerEvents(FreezeAll(), this)
        server.pluginManager.registerEvents(SpeedyBlocks(), this)
        server.pluginManager.registerEvents(EventIGNs(), this)
        server.pluginManager.registerEvents(UseChat(), this)
        server.pluginManager.registerEvents(ApplyResourcepack(), this)

        // Initialize classes
        RenderDistance() // Server settings
        NoCraft() // Gameplay setting
        LimitEnchant() // Gameplay setting
        AntiBlockGlitching() // Patch
        AntiEnderPearl() // Patch

        // Season specific load
        NE1_Load(this)
    }

    // Plugin shutdown logic
    override fun onDisable() { }

    // Function to get APIs
    private fun getAPIs() {
        // TAB API
        TAB_INSTANCE = TabAPI.getInstance()
        TAB_NAMETAG_MANAGER = TAB_INSTANCE.nameTagManager!!
        TAB_HEADER_FOOTER_MANAGER = TAB_INSTANCE.headerFooterManager!!
        if (TAB_INSTANCE != null) logger.info("Connected to TAB API") else logger.warning("Can't connect to TAB API")

        // LUCKPERMS API
        LP_INSTANCE = LuckPermsProvider.get()
        LP_GROUP_MANAGER = LP_INSTANCE.groupManager
        if (LP_INSTANCE != null) logger.info("Connected to LuckPerms API") else logger.warning("Can't connect to LuckPerms API")
    }
}
