package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.FilesManager
import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import java.io.File
import java.util.UUID

class PlayerGroupChatManager : Listener {

    init { loadGroupChats() }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) { saveGroupChats() }

    companion object {
        val ENABLED = NationsEvent.INSTANCE.config.getBoolean("player-group-chats-enable")
        val GROUPCHATCOLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-color")
        val GROUPCHATSPYCOLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-spy-color")
        val BYPASSDISABLEDCHAT = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-bypass-disabled-chat")

        val GROUP_CHATS = mutableMapOf<Int, MutableList<OfflinePlayer>>()
        val INVITES = mutableMapOf<Int, MutableList<Player>>()
        val STAFF_SPIES = mutableListOf<Player>()
    }

    private fun loadGroupChats() {
        if (!ENABLED) return

        NationsEvent.INSTANCE.logger.info("Loading player group chats...")

        FilesManager.createFile(Saves.FILE_PLAYER_GROUP_CHATS)

        NationsEvent.INSTANCE.getCommand("groupchat")?.apply { setExecutor(PlayerGroupChatCommand()); tabCompleter = PlayerGroupChatCommand() }
        NationsEvent.INSTANCE.getCommand("admingroupchat")?.apply { setExecutor(AdminGroupChatCommand()); tabCompleter = AdminGroupChatCommand() }

        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)

        loadGroupChatsFromFile(Saves.FILE_PLAYER_GROUP_CHATS)
    }

    private fun loadGroupChatsFromFile(file: File) {
        YamlConfiguration.loadConfiguration(file).getValues(true).forEach {
            (p, v) -> run {
                if (v.javaClass != mutableListOf("").javaClass) return@run
                GROUP_CHATS[p.toInt()] = (v as List<*>).map { n -> Bukkit.getOfflinePlayer(UUID.fromString(n.toString())) }.toMutableList()
            }
        }
    }

    private fun saveGroupChats() {
        if (!ENABLED) return

        NationsEvent.INSTANCE.logger.info("Saving player group chats to YML...")

        val gcFile = YamlConfiguration.loadConfiguration(Saves.FILE_PLAYER_GROUP_CHATS)
        GROUP_CHATS.forEach { (k, v) -> gcFile.set(k.toString(), v.map { it.uniqueId.toString() }) }
        gcFile.save(Saves.FILE_PLAYER_GROUP_CHATS)
    }
}