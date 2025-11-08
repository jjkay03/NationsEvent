package com.jjkay03.nationsevent.chat.group_chat

import com.jjkay03.nationsevent.Saves

enum class GroupChats (
    val prefix: String,
    val formatting: String,
    val permissionSend: String,
    val permissionView: String,
    val command: Set<String>,
) {

    /*
        This enum is to create group chats based on permission.
        The entries of this enum are the group chats. To create a GC create a new entry!

        Prefix: This is displayed before every message in the chat.
        Formatting: Use %player% for where the player name shows and %message% for where the message should show.
        Permission Send: This is the perm needed to send a message in that GC.
        Permission View: This is the perm needed to view messages in that GC.
        Commands: This is a list of commands that can be used to message in the GC.
    */

    STAFF_CHAT (
        "§4[SC] ",
        "§c%player%: %message%",
        Saves.PERM_STAFF,
        Saves.PERM_STAFF,
        setOf("staffchat", "sc")
    )
}