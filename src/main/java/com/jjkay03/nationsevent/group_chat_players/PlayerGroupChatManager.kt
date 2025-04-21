package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.FilesManager
import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.group_chat_players.commands.*
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin

class PlayerGroupChatManager(private val plugin: JavaPlugin): Listener {

    companion object {
        // Get config settings
        val ENABLED = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-enable", false)
        val SAVE_ON_SERVER_RESTART = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-save-on-server-restart", false)
        val GROUP_CHAT_LIMIT = NationsEvent.INSTANCE.config.getInt("player-group-chat-limit", 1)
        val BYPASS_DISABLED_CHAT = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-bypass-disabled-chat", false)
        val GROUP_CHAT_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-color", "§7")
        val GROUP_CHAT_SPY_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-spy-color", "§8")
        val STAFF_MESSAGE_PREFIX = NationsEvent.INSTANCE.config.getString("player-group-chat-staff-msg-prefix", "§6[STAFF]")
        val STAFF_MESSAGE_PREFIX_FORMATLESS = Utils.removeFormattingCodes(STAFF_MESSAGE_PREFIX)
        val NAME_CHARACTER_LIMIT = NationsEvent.INSTANCE.config.getInt("player-group-chat-name-character-limit", 6)

        // Permission
        val PERM_BYPASS_LIMIT: Permission = Permission("nationsevent.playergroupchat.bypasslimit")

        // Variables
        const val PREFIX = "\uD83D\uDC65\uD83D\uDCAC " // Prefix used before all command feedback
        val COMMANDS = setOf("groupchat", "admingroupchat", "debuggroupchat")
        val GROUP_CHATS = mutableListOf<PlayerGroupChat>()
        val PLAYERS_SELECTED_GC = mutableMapOf<OfflinePlayer, Int>()
        val GLOBAL_SPIES = mutableListOf<OfflinePlayer>()
    }

    // Run on class initialization
    init {
        if (ENABLED) loadGroupChats()
        else Utils.disableCommands(COMMANDS, "Players Group Chats")
    }

    // Run on plugin disable to save group chats to yml
    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        if (SAVE_ON_SERVER_RESTART) PlayerGroupChatSave.saveGCToFile(Saves.FILE_PLAYER_GROUP_CHATS, GROUP_CHATS)
    }

    // Function to load the player group chat
    fun loadGroupChats() {
        // Log message
        NationsEvent.INSTANCE.logger.info("Loading player group chats...")

        // Create default player group chat files
        FilesManager.createDirectory(Saves.DIR_PLAYER_GC)
        FilesManager.createDirectory(Saves.DIR_PLAYER_GC_LOGS)
        FilesManager.createFile(Saves.LOG_FILE_PLAYER_GC)
        FilesManager.createFile(Saves.FILE_PLAYER_GROUP_CHATS)

        // Class variables
        val playerGroupChatCommand = PlayerGroupChatCommand()
        val adminGroupChatCommand = AdminGroupChatCommand()
        val debugGroupChatCommand = DebugGroupChatCommand()

        // Register commands
        plugin.getCommand("groupchat")?.apply { setExecutor(playerGroupChatCommand); tabCompleter = playerGroupChatCommand }
        plugin.getCommand("admingroupchat")?.apply { setExecutor(adminGroupChatCommand); tabCompleter = adminGroupChatCommand }
        plugin.getCommand("debuggroupchat")?.apply { setExecutor(debugGroupChatCommand); tabCompleter = debugGroupChatCommand }

        // Register events
        plugin.server.pluginManager.registerEvents(this, plugin)

        // Load saved player group chats from yml file
        if (SAVE_ON_SERVER_RESTART) GROUP_CHATS.addAll(PlayerGroupChatSave.importGCFromFile(Saves.FILE_PLAYER_GROUP_CHATS))
    }
}