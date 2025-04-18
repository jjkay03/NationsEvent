package com.jjkay03.nationsevent.group_chat_players

import org.bukkit.OfflinePlayer

data class PlayerGroupChat(
    val id: Int,
    var owner: OfflinePlayer,
    val playerList: MutableList<OfflinePlayer> = mutableListOf(owner),
    val invites: MutableList<OfflinePlayer> = mutableListOf(),
    val spies: MutableList<OfflinePlayer> = mutableListOf(),
    val name: String = "GC$id"
)