package com.jjkay03.nationsevent.chat.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.OfflinePlayer

object PlayerGroupChatLog {

    private fun log(msg: String) {
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "PGC", msg)
    }

    fun chatInGC(groupChat: PlayerGroupChat, player: OfflinePlayer?, message: String, staffAction: Boolean = false) {
        val author = when {
            player == null -> ""
            staffAction -> "${Config.PGC_STAFF_MESSAGE_PREFIX_FORMATLESS} ${player.name}: "
            else -> "${player.name}: "
        }
        log("[CHAT] [${groupChat.prefix}] $author$message")
    }

    fun createGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "${Config.PGC_STAFF_MESSAGE_PREFIX_FORMATLESS} " else groupChat.owner.name} CREATED group chat ${groupChat.prefix}")
    }

    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "${Config.PGC_STAFF_MESSAGE_PREFIX_FORMATLESS} " else groupChat.owner.name} DELETED group chat ${groupChat.prefix}")
    }

    fun addPlayerToGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        if (staffAction) { log("${Config.PGC_STAFF_MESSAGE_PREFIX_FORMATLESS} ADDED ${player.name} to group chat ${groupChat.prefix}") }
        else { log("${player.name} JOINED group chat ${groupChat.name}") }
    }

    fun removePlayerFromGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        log("${if (staffAction) "${Config.PGC_STAFF_MESSAGE_PREFIX_FORMATLESS} " else groupChat.owner.name} REMOVED ${player.name} from group chat ${groupChat.prefix}")
    }

    fun invitePlayerToGC(groupChat: PlayerGroupChat, player: OfflinePlayer) {
        log("${groupChat.owner.name} INVITED ${player.name} to group chat ${groupChat.prefix}")
    }
}