package com.jjkay03.nationsevent.group_chat_players

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jjkay03.nationsevent.FilesManager
import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.group_chat_players.commands.*
import org.bukkit.OfflinePlayer
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PlayerGroupChatManager(private val plugin: JavaPlugin): Listener {

    companion object {
        // Get config settings
        val ENABLED = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-enable")
        val GROUP_CHAT_LIMIT = NationsEvent.INSTANCE.config.getInt("player-group-chat-limit")
        val BYPASS_DISABLED_CHAT = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-bypass-disabled-chat")
        val GROUP_CHAT_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-color")
        val GROUP_CHAT_SPY_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-spy-color")
        val STAFF_MESSAGE_PREFIX = NationsEvent.INSTANCE.config.getString("player-group-chat-staff-msg-prefix")
        val STAFF_MESSAGE_PREFIX_FORMATLESS = Utils.removeFormattingCodes(STAFF_MESSAGE_PREFIX)
        val NAME_CHARACTER_LIMIT = NationsEvent.INSTANCE.config.getInt("player-group-chat-name-character-limit")

        // Variables
        val COMMANDS = setOf("groupchat", "admingroupchat")
        val GROUP_CHATS = mutableListOf<PlayerGroupChat>()
        val PLAYERS_SELECTED_GC = mutableMapOf<OfflinePlayer, Int>()
        val UNIVERSAL_SPIES = mutableListOf<OfflinePlayer>()
    }

    // Run on class initialization
    init {
        if (ENABLED) loadGroupChats()
        else Utils.disableCommands(COMMANDS, "Players Group Chats")
    }

    // TODO: DISABLED TEMP BECAUSE NOT TESTED
    // Run on plugin disable to save group chats to yml
    //@EventHandler
    //fun onPluginDisable(event: PluginDisableEvent) { saveGCToFile(Saves.FILE_PLAYER_GROUP_CHATS, GROUP_CHATS) }

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

        // Register commands
        plugin.getCommand("groupchat")?.apply { setExecutor(playerGroupChatCommand); tabCompleter = playerGroupChatCommand }
        plugin.getCommand("admingroupchat")?.apply { setExecutor(adminGroupChatCommand); tabCompleter = adminGroupChatCommand }

        // Register events
        plugin.server.pluginManager.registerEvents(this, plugin)

        // TODO: DISABLED TEMP BECAUSE NOT TESTED
        // Load saved player group chats from yml file
        //GROUP_CHATS.addAll(importGCFromFile(Saves.FILE_PLAYER_GROUP_CHATS))
    }

    // TODO: NEEDS TESTING
    // Function to save group chats to json file
    fun saveGCToFile(file: File, groupChatsList: List<PlayerGroupChat>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(groupChatsList)
        file.writeText(json)
    }

    // TODO: NEEDS TESTING
    // Function to import group chats from json file
    fun importGCFromFile(file: File): List<PlayerGroupChat> {
        val gson = Gson()
        if (!file.exists()) return emptyList()
        val reader = file.bufferedReader()
        val type = object : TypeToken<List<PlayerGroupChat>>() {}.type
        return gson.fromJson(reader, type)
    }
}