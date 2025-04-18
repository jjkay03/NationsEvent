package com.jjkay03.nationsevent.group_chat_players

import org.bukkit.OfflinePlayer

import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS

object PlayerGroupChatUtils {

    /*

   chat : send message to all group chat members + spies (separate methods)
       - chat to gc
       - chat to spies
       - [GC<ID>] <player>: <message>

   create gc : make a new group chat; prevent if already in one
   delete gc : delete group chat (if owner)

   add to gc (playerList) : add a player to gc
   remove from gc (playerList) : remove a player from gc

   coords gc : send your coords in gc chat
   list gc members : send get group chat name in chat (of all gcs player is in)

   invite : invite a player to the gc
   join gc : add yourself to a gc if you were invited
   leave gc : leave current gc
   kick gc : remove someone else from gc (if owner)

   transfer gc : make a diff player the gc owner (if owner)

   get owner : returns the owner
   get groupchat name : returns a component with "GC<ID>" that is hoverable, displaying all members

   all in one function to manage the multiple gcs : checks if player is in multiple gcs TODO

   implement logging !!

     */

    // Function that gets the smallest available ID (used when creating new GCs)
    fun getNewGCID() : Int {

        // Gets the first non-used int to become the index
        val ids = mutableListOf<Int>()
        GROUP_CHATS.forEach { ids.add(it.id) }
        ids.sort()

        // Gets the first non-used int to become the index
        var lastIndex = -1
        for (index in ids) { if (index - lastIndex != 1) { break }; lastIndex += 1 }

        // Returns the smallest available Int
        return lastIndex + 1
    }

    // Function that gets all the group chats 'player' is in
    fun getPlayerGC(player: OfflinePlayer): List<PlayerGroupChat> {
        val gcList = mutableListOf<PlayerGroupChat>()
        GROUP_CHATS.forEach { if (it.playerList.contains(player)) gcList.add(it) }
        return gcList
    }

    // Function to creates a new group chat with 'owner' as its owner and 'players' as the players
    fun createGC(owner: OfflinePlayer, players: List<OfflinePlayer> = listOf(), staffAction: Boolean = false) : PlayerGroupChat {
        val groupChat = PlayerGroupChat(getNewGCID(), owner)
        groupChat.playerList.addAll(players)
        GROUP_CHATS.add(groupChat)

        PlayerGroupChatLog.createGC(groupChat, staffAction)
        return groupChat
    }

    // Function to deletes a group chat
    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        GROUP_CHATS.remove(groupChat)

        // Log action
        PlayerGroupChatLog.deleteGC(groupChat, staffAction)
    }

    // Function to removes 'player' from the given 'groupChat'
    fun removePlayerFromGC(groupChat: PlayerGroupChat, player: OfflinePlayer, staffAction: Boolean = false) {
        groupChat.playerList.remove(player)

        // Delete the GC if there are no more players
        if (groupChat.playerList.isEmpty()) { deleteGC(groupChat); return }

        // If 'player' was the owner, sets the first in 'playerList' as the new owner
        if (groupChat.owner == player) groupChat.owner = groupChat.playerList.first()

        // Log action
        PlayerGroupChatLog.removePlayerFromGC(groupChat, player, staffAction)
    }
}