package com.jjkay03.nationsevent.group_chat_players.deprecated

import com.jjkay03.nationsevent.FilesManager
import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class Deprecated_PlayerGroupChatManager (private val plugin: JavaPlugin) : Listener {

    companion object {
        // Get config settings
        val ENABLED = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-enable")
        val BYPASS_DISABLED_CHAT = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-bypass-disabled-chat")
        val GROUP_CHAT_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-color")
        val GROUP_CHAT_SPY_COLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-spy-color")
        val STAFF_MESSAGE_PREFIX = NationsEvent.INSTANCE.config.getString("player-group-chat-staff-msg-prefix")
        val STAFF_MESSAGE_PREFIX_FORMATLESS = Utils.removeFormattingCodes(STAFF_MESSAGE_PREFIX)

        // Variables
        val COMMANDS = setOf("groupchat", "admingroupchat")
        val GROUP_CHATS = mutableMapOf<Int, MutableList<OfflinePlayer>>()
        val INVITES = mutableMapOf<Int, MutableList<Player>>()
        val STAFF_SPIES = mutableMapOf<Int, MutableList<Player>>(-1 to mutableListOf())
    }

    // Run on class initialization
    init {
        if (ENABLED) loadPlayerGroupChat()
        else Utils.disableCommands(COMMANDS, "Players Group Chats")
    }

    // Run on plugin disable to save group chats to yml
    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) { saveGroupChats() }

    // Function to load player group chat feature
    private fun loadPlayerGroupChat() {
        // Log message
        NationsEvent.INSTANCE.logger.info("Loading player group chats...")

        // Create default player group chat files
        FilesManager.createDirectory(Saves.DIR_PLAYER_GC)
        FilesManager.createDirectory(Saves.DIR_PLAYER_GC_LOGS)
        FilesManager.createFile(Saves.LOG_FILE_PLAYER_GC)
        FilesManager.createFile(Saves.FILE_PLAYER_GROUP_CHATS)

        // Class variables
        val playerGroupChatCommand = Deprecated_PlayerGroupChatCommand()
        val adminGroupChatCommand = AdminGroupChatCommand()

        // Register commands
        plugin.getCommand("groupchat")?.apply { setExecutor(playerGroupChatCommand); tabCompleter = playerGroupChatCommand }
        plugin.getCommand("admingroupchat")?.apply { setExecutor(adminGroupChatCommand); tabCompleter = adminGroupChatCommand }

        // Register events
        plugin.server.pluginManager.registerEvents(this, plugin)

        // Load saved player group chats from yml file
        loadGroupChatsFromFile(Saves.FILE_PLAYER_GROUP_CHATS)
    }

    // Function to load saved player group chats from yml file
    private fun loadGroupChatsFromFile(file: File) {
        YamlConfiguration.loadConfiguration(file).getValues(true).forEach {
            (p, v) -> run {
                if (v.javaClass != mutableListOf("").javaClass) return@run

                // Reconstruct yml data into group chat map of Int and Player
                GROUP_CHATS[p.toInt()] = (v as List<*>).map { n -> Bukkit.getOfflinePlayer(UUID.fromString(n.toString())) }.toMutableList()
                INVITES[p.toInt()] = mutableListOf()
                STAFF_SPIES[p.toInt()] = mutableListOf()
            }
        }
    }

    // Function to save player group chats map to yml fil
    private fun saveGroupChats() {
        if (!ENABLED) return
        NationsEvent.INSTANCE.logger.info("Saving player group chats to YML...")
        val gcFile = YamlConfiguration.loadConfiguration(Saves.FILE_PLAYER_GROUP_CHATS)
        gcFile.getKeys(false).forEach { key -> gcFile.set(key, null) }
        GROUP_CHATS.forEach { (k, v) -> gcFile.set(k.toString(), v.map { it.uniqueId.toString() }) }
        gcFile.save(Saves.FILE_PLAYER_GROUP_CHATS)
    }
}