package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.OfflinePlayer

object PlayerGroupChatLog {

    private fun log(msg: String) {
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", msg)
    }

    fun createGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else groupChat.owner} CREATED group chat ${groupChat.name}")
    }

    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else groupChat.owner} DELETED group chat ${groupChat.name}")
    }

    fun removePlayerFromGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        log("${if (staffAction) "$STAFF_MESSAGE_PREFIX_FORMATLESS " else groupChat.owner} REMOVED ${player.name} from group chat ${groupChat.name}")
    }
}