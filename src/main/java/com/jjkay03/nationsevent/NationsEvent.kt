package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.commands.others.*
import com.jjkay03.nationsevent.commands.player_scale.*
import com.jjkay03.nationsevent.commands.voicechat.*
import com.jjkay03.nationsevent.commands.voting.*
import com.jjkay03.nationsevent.economy.Economy
import com.jjkay03.nationsevent.features.*
import com.jjkay03.nationsevent.gameplay.*
import com.jjkay03.nationsevent.group_chat.GroupChatsCommands
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager
import com.jjkay03.nationsevent.patches.*
import com.jjkay03.nationsevent.specific_event.SpecificEvent
import com.jjkay03.nationsevent.server_settings.*
import com.jjkay03.nationsevent.utils.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.GroupManager
import net.luckperms.api.model.user.UserManager
import org.ipvp.canvas.MenuFunctionListener

open class NationsEvent : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: NationsEvent

        // LUCKPERMS API
        lateinit var LP_INSTANCE: LuckPerms
        lateinit var LP_GROUP_MANAGER: GroupManager
        lateinit var LP_USER_MANAGER: UserManager
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
        FilesManager.createDefaultDirectories() // Create default directories
        FilesManager.createDefaultFiles() // Create default files

        // Class variable
        val hideStaffCommand = HideStaffCommand()

        // Register commands
        GroupChatsCommands.registerGroupChatCommands(this)
        getCommand("nationseventtest")?.apply { setExecutor(NationsEventTestCommand()); tabCompleter = NationsEventTestCommand() }
        getCommand("joinvc")?.apply { setExecutor(JoinvcCommand()) }
        getCommand("joinstage")?.apply { setExecutor(JoinStageCommand()) }
        getCommand("announcesession")?.apply { setExecutor(AnnounceSessionCommand()) }
        getCommand("sessiontime")?.apply { setExecutor(SessionTimeCommand()) }
        getCommand("pvptoggle")?.apply { setExecutor(PVPToggleCommand()); tabCompleter = PVPToggleCommand() }
        getCommand("pvpalerts")?.apply { setExecutor(PVPAlertsCommand()) }
        getCommand("voicechatperms")?.apply { setExecutor(VoicechatPermsCommand()); tabCompleter = VoicechatPermsCommand() }
        getCommand("groupvoicechatperm")?.apply { setExecutor(GroupVoicechatPermCommand()); tabCompleter = GroupVoicechatPermCommand() }
        getCommand("bypassviewdistance")?.apply { setExecutor(BypassViewDistanceCommand()) }
        getCommand("vote")?.apply { setExecutor(VoteCommand()) }
        getCommand("topvotes")?.apply { setExecutor(TopVotesCommand()) }
        getCommand("clearvotes")?.apply { setExecutor(ClearVotesCommand()) }
        getCommand("exportvotes")?.apply { setExecutor(ExportVotesCommand()) }
        getCommand("lockvotes")?.apply { setExecutor(LockVotesCommand()) }
        getCommand("playerscale")?.apply { setExecutor(PlayerScaleCommand()) }
        getCommand("playerscalerestall")?.apply { setExecutor(PlayerScaleRestAllCommand()) }
        getCommand("needadmin")?.apply { setExecutor(NeedAdminCommand()); tabCompleter = NeedAdminCommand() }
        getCommand("freezeall")?.apply { setExecutor(FreezeAllCommand()) }
        getCommand("hidestaff")?.apply { setExecutor(hideStaffCommand) }
        getCommand("fullmoon")?.apply { setExecutor(FullMoonCommand()) }
        getCommand("permanentmessage")?.apply { setExecutor(PermanentMessageCommand()) }
        getCommand("admingui")?.apply { setExecutor(AdminGUICommand()) }
        getCommand("globalchat")?.apply { setExecutor(GlobalChatCommand()); tabCompleter = GlobalChatCommand() }
        getCommand("applyserverpack")?.apply { setExecutor(ApplyServerPackCommand()) }
        getCommand("restockvillagers")?.apply { setExecutor(RestockVillagers()) }
        getCommand("randomplayertp")?.apply { setExecutor(RandomPlayerTPCommand()) }

        // Register events
        Bukkit.getPluginManager().registerEvents(MenuFunctionListener(), this) // Canvas MenuFunctionListener
        server.pluginManager.registerEvents(hideStaffCommand, this)
        server.pluginManager.registerEvents(PVPToggle(), this)
        server.pluginManager.registerEvents(PVPAlerts(), this)
        server.pluginManager.registerEvents(MeatPlayerDeath(), this)
        server.pluginManager.registerEvents(IronDoor(), this)
        server.pluginManager.registerEvents(FarmProtection(), this)
        server.pluginManager.registerEvents(NoWolfBreeding(), this)
        server.pluginManager.registerEvents(FreezeAll(), this)
        server.pluginManager.registerEvents(SpeedyBlocks(), this)
        server.pluginManager.registerEvents(EventIGNs(), this)
        server.pluginManager.registerEvents(UseChat(), this)
        server.pluginManager.registerEvents(ApplyResourcepack(), this)

        // Initialize classes
        Economy(this)                  // Economy
        PlayerGroupChatManager(this)   // Group chats
        RenderDistance()                       // Server settings
        NoCraft()                              // Gameplay setting
        LimitEnchant()                         // Gameplay setting
        NoEnderPearl()                         // Gameplay setting
        AntiBlockGlitching()                   // Patch

        // Load specific event classes
        SpecificEvent(this)
        PermToLPGroup(this) // Patch for DiscordSRV role link not working

        // Register placeholder (PlaceholderAPI)
        registerPlaceholderAPI()
    }

    // Plugin shutdown logic
    override fun onDisable() {
        logger.info("Bye bye!")
    }

    // Function to get APIs
    private fun getAPIs() {
        // LUCKPERMS API
        LP_INSTANCE = LuckPermsProvider.get()
        LP_GROUP_MANAGER = LP_INSTANCE.groupManager
        LP_USER_MANAGER = LP_INSTANCE.userManager
        @Suppress("SENSELESS_COMPARISON")
        if (LP_INSTANCE != null) logger.info("Connected to LuckPerms API") else logger.severe("Can't connect to LuckPerms API")
    }

    // Register PlaceholderAPI expansion if found
    private fun registerPlaceholderAPI() {
        server.pluginManager.getPlugin("PlaceholderAPI")
            ?.let { logger.info("Placeholders registered successfully!"); Placeholders().register() }
            ?: logger.severe("PlaceholderAPI not found, placeholders will not be registered!")
    }
}
