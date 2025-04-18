package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.OfflinePlayer

object PlayerGroupChatLog {

    private fun log(msg: String) {
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "PGC", msg)
    }

    fun createGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else groupChat.owner} CREATED group chat ${groupChat.name}")
    }

    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else groupChat.owner} DELETED group chat ${groupChat.name}")
    }

    fun addPlayerToGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        if (staffAction) { log("$STAFF_MESSAGE_PREFIX_FORMATLESS ADDED ${player.name} to group chat ${groupChat.name}") }
        else { log("${player.name} JOINED group chat ${groupChat.name}") }
    }

    fun removePlayerFromGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        log("${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else groupChat.owner} REMOVED ${player.name} from group chat ${groupChat.name}")
    }

    fun invitePlayerToGC(groupChat: PlayerGroupChat, player: OfflinePlayer) {
        log("${groupChat.owner} INVITED ${player.name} to group chat ${groupChat.name}")
    }

    fun chatInGC(groupChat: PlayerGroupChat, player: OfflinePlayer, message: String, staffAction: Boolean = false) {
        log("[${groupChat.name}] ${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else ""} ${player.name}: $message")
    }
}