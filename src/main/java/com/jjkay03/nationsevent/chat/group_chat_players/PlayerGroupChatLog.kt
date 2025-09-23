package com.jjkay03.nationsevent.chat.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.OfflinePlayer

object PlayerGroupChatLog {
    // Remove formating from staff message prefix
    val staffPrefix = Utils.removeFormattingCodes(Config.PGC_STAFF_MESSAGE_PREFIX)

    // Helper function to add log messages to log file
    private fun log(msg: String) {
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "PGC", msg)
    }

    // Function to log group chat message
    fun chatInGC(groupChat: PlayerGroupChat, player: OfflinePlayer?, message: String, staffAction: Boolean = false) {
        val author = when {
            player == null -> ""
            staffAction -> "$staffPrefix ${player.name}: "
            else -> "${player.name}: "
        }
        log("[CHAT] [${groupChat.prefix}] $author$message")
    }

    // Function to log group chat creation
    fun createGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "$staffPrefix " else groupChat.owner.name} CREATED group chat ${groupChat.prefix}")
    }

    // Function to log group chat delete
    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        log("${if (staffAction) "$staffPrefix " else groupChat.owner.name} DELETED group chat ${groupChat.prefix}")
    }

    // Function to log player add to group chat
    fun addPlayerToGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        if (staffAction) { log("$staffPrefix ADDED ${player.name} to group chat ${groupChat.prefix}") }
        else { log("${player.name} JOINED group chat ${groupChat.name}") }
    }

    // Function to log player remove from group chat
    fun removePlayerFromGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        log("${if (staffAction) "$staffPrefix " else groupChat.owner.name} REMOVED ${player.name} from group chat ${groupChat.prefix}")
    }

    // Function to log player invite to group chat
    fun invitePlayerToGC(groupChat: PlayerGroupChat, player: OfflinePlayer) {
        log("${groupChat.owner.name} INVITED ${player.name} to group chat ${groupChat.prefix}")
    }
}