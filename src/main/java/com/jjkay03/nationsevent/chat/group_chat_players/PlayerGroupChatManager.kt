package com.jjkay03.nationsevent.chat.group_chat_players

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.FilesManager
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.chat.group_chat_players.commands.AdminGroupChatCommand
import com.jjkay03.nationsevent.chat.group_chat_players.commands.PlayerGroupChatCommand
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.permissions.Permission

class PlayerGroupChatManager: Listener {

    companion object {
        // Variables
        const val PREFIX = "\uD83D\uDC65\uD83D\uDCAC " // Prefix used before all command feedback
        val COMMANDS = setOf("groupchat", "admingroupchat")
        val GROUP_CHATS = mutableListOf<PlayerGroupChat>()
        val PLAYERS_SELECTED_GC = mutableMapOf<OfflinePlayer, Int>()
        val GLOBAL_SPIES = mutableListOf<OfflinePlayer>()

        // Permission
        val PERM_BYPASS_LIMIT: Permission = Permission("nationsevent.playergroupchat.bypasslimit")
    }

    // Run on class initialization
    init {
        if (Config.PGC_ENABLED) loadGroupChats()
        else Utils.disableCommands(COMMANDS, "Players Group Chats")
    }

    // Run on plugin disable to save group chats to yml
    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        if (Config.PGC_SAVE_ON_SERVER_RESTART) PlayerGroupChatSave.saveGCToFile(Saves.FILE_PLAYER_GROUP_CHATS, GROUP_CHATS)
    }

    // Function to load the player group chat
    fun loadGroupChats() {
        // Create default player group chat files
        FilesManager.createDirectory(Saves.DIR_PLAYER_GC)
        FilesManager.createDirectory(Saves.DIR_PLAYER_GC_LOGS)
        FilesManager.createFile(Saves.LOG_FILE_PLAYER_GC)
        FilesManager.createFile(Saves.FILE_PLAYER_GROUP_CHATS)

        // Class variables
        val playerGroupChatCommand = PlayerGroupChatCommand()
        val adminGroupChatCommand = AdminGroupChatCommand()

        // Register commands
        NationsEvent.INSTANCE.getCommand("groupchat")?.apply { setExecutor(playerGroupChatCommand); tabCompleter = playerGroupChatCommand }
        NationsEvent.INSTANCE.getCommand("admingroupchat")?.apply { setExecutor(adminGroupChatCommand); tabCompleter = adminGroupChatCommand }

        // Register events
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)

        // Load saved player group chats from yml file
        if (Config.PGC_SAVE_ON_SERVER_RESTART) GROUP_CHATS.addAll(PlayerGroupChatSave.importGCFromFile(Saves.FILE_PLAYER_GROUP_CHATS))

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading Player Group Chats (${GROUP_CHATS.size})")
    }
}