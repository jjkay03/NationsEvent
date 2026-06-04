package com.jjkay03.nationsevent

import com.jjkay03.nationsevent.chat.*
import com.jjkay03.nationsevent.chat.group_chat.*
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChatManager
import com.jjkay03.nationsevent.commands.Commands
import com.jjkay03.nationsevent.features.*
import com.jjkay03.nationsevent.integrations.luckperms.LuckPermsManager
import com.jjkay03.nationsevent.integrations.voicechat.SimpleVoiceChat
import com.jjkay03.nationsevent.settings.*
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.FilesManager
import com.jjkay03.nationsevent.utils.ServerType
import com.jjkay03.nationsevent.worlds.WorldLobby
import com.jjkay03.nationsevent.worlds.WorldsBridge
import com.jjkay03.nationsevent.worlds.WorldsLoader
import com.jjkay03.nationsevent.worlds.WorldsSync
import org.bukkit.plugin.java.JavaPlugin

open class NationsEvent : JavaPlugin() {
    
    // COMPANIONS
    companion object {
        lateinit var INSTANCE: NationsEvent
    }

    // PLUGIN STARTUP LOGIC
    override fun onEnable() {
        INSTANCE = this

        // STARTUP INFO
        ServerType.detect()
        Utils.pluginWelcomeMessage("§e")

        // CONFIGURATION
        saveDefaultConfig()            // Save default config
        reloadConfig()                 // Reload default config
        Config()                       // Load config settings

        // STARTUP
        LuckPermsManager()             // Get LuckPerms API
        Saves()                        // Load all variables in saves class
        FilesManager.createDefaults()  // Generate default directories and files

        // REGISTER COMMANDS
        logger.info("REGISTER AND LOAD ALL FEATURES:")
        Commands()

        // INTEGRATIONS
        if (Config.INTEGRATIONS_VOICECHAT) SimpleVoiceChat()

        // CHAT
        ChatManager()
        PlayerGroupChatManager()
        GroupChatsCommands()

        // WORLDS
        WorldsLoader()
        WorldsSync()
        WorldLobby()
        WorldsBridge()

        // SETTINGS
        DisabledCrafts()
        DisableCrafterCrafts()
        LimitEnchants()
        DisableEnderPearls()
        DisableWolfBreeding()
        FarmProtection()
        DisableJoinLeaveMessages()
        DisableHostileMobSpawn()
        DisableGoatHornDrop()
        DisableEquipPlayerHead()

        // FEATURES
        DeathBan()
        EventIGNs()
        BoatPVP()
        DeathDropHead()

        // EVENT SPECIFIC
        EventSpecific()

    }

    // PLUGIN SHUTDOWN LOGIC
    override fun onDisable() {
        logger.info("Bye bye!")
    }
}