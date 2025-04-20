package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.BYPASS_DISABLED_CHAT
import org.bukkit.OfflinePlayer
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHAT_COLOR
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHAT_SPY_COLOR
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHAT_LIMIT
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.NAME_CHARACTER_LIMIT
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.PREFIX
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.PLAYERS_SELECTED_GC
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.UNIVERSAL_SPIES
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.entity.Player
import kotlin.collections.set

object PlayerGroupChatUtils {

    /*
   ✅ chat : send message to all group chat members + spies (separate methods)
       ✅ - chat to gc
       ✅ - chat to spies
       ✅ - [GC<ID>] <player>: <message>

   ✅ create gc : make a new group chat; prevent if already in one
   ✅ delete gc : delete group chat (if owner)

   ✅ add to gc (playerList) : add a player to gc
   ✅ remove from gc (playerList) : remove a player from gc

   ✅ coords gc : send your coords in gc chat
   ⬜ list gc members : send get group chat name in chat (of all gcs player is in) | DO DIRECTLY IN COMMAND USING formatHoverableMessage()

   ✅ invite : invite a player to the gc
   ⬜ join gc : add yourself to a gc if you were invited | DO DIRECTLY IN COMMAND USING addPlayerToGC
   ⬜ leave gc : leave current gc  |  DO DIRECTLY IN COMMAND USING removePlayerFromGC
   ⬜ kick gc : remove someone else from gc (if owner)  |  DO DIRECTLY IN COMMAND USING removePlayerFromGC

   ⬜ transfer gc : make a diff player the gc owner (if owner) | DO DIRECTLY IN COMMAND USING groupChat.owner = newOwner

   ⬜ get owner : returns the owner  |  JUST USE "PlayerGroupChat.owner"
   ✅ get group chat name : returns a component with "GC<ID>" that is hoverable, displaying all members
   ✅ get group chat from id : returns a group chat using an id

   ✅ all in one function to manage the multiple gcs : checks if player is in multiple gcs
   ✅ group chat selection system

   ✅ implement logging

   ✅ Create function that returns a list of gc the player is in formated for tab complete
      (Example: "1-name", "3-fun", "5", "14-test", "55") the id will be extracted use to
      determine what gc they want to perform the action in

     */

    // Use to signify the state of a player group chats limit
    enum class LimitState { VALID, LIMIT, EXCEEDED }

    // Function used to send message in a group chat
    fun chat(groupChat: PlayerGroupChat, player: Player?, message: String, staffAction: Boolean = false) {
        // Format messages
        val prefix = "$GROUP_CHAT_COLOR[%gc%$GROUP_CHAT_COLOR] "
        val author = when {
            player == null -> ""
            (staffAction) -> "$STAFF_MESSAGE_PREFIX$GROUP_CHAT_COLOR ${player.name}: "
            else -> "${player.name}: "
        }
        val playerMsg = formatHoverableMessage("$prefix$author$message", GROUP_CHAT_COLOR, groupChat, false)
        val spiesMsg  = formatHoverableMessage("$prefix$author$message", GROUP_CHAT_SPY_COLOR, groupChat, false)

        // Player is unable to chat in GCs if they do not have the 'PERM_USE_CHAT' permission UNLESS 'BYPASS_DISABLED_CHAT' is true
        if (player != null && !player.hasPermission(Saves.PERM_USE_CHAT) && !BYPASS_DISABLED_CHAT) { player.sendMessage("§cChat is disabled!"); return }

        Utils.sendMessageToPlayerList(groupChat.playerList, playerMsg)
        Utils.sendMessageToPlayerList(groupChat.spies, spiesMsg)
        Utils.sendMessageToPlayerList(UNIVERSAL_SPIES, spiesMsg)

        // Log
        PlayerGroupChatLog.chatInGC(groupChat, player, message, staffAction)
    }

    // Function to send 'player' coordinates in 'groupChat'
    fun chatCoords(groupChat: PlayerGroupChat, player: Player, staffAction: Boolean = false) {
        chat(groupChat, player, "${player.location.blockX} / ${player.location.blockY} / ${player.location.blockZ}", staffAction)
    }

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

    // Function that looks for a group chat using an ID
    fun getGCfromID(id: Int): PlayerGroupChat? {
        for (groupChat in GROUP_CHATS) { if (groupChat.id == id) return groupChat }
        return null // Return null if no group chat with 'id' found
    }

    // Function that gets all the group chats 'player' is in
    fun getPlayerGCs(player: OfflinePlayer, ownedGroupChatOnly: Boolean = false): List<PlayerGroupChat> {
        val gcList = mutableListOf<PlayerGroupChat>()
        GROUP_CHATS.forEach { groupChat ->
            // Only check ownership when ownedGroupChatOnly is true
            if (ownedGroupChatOnly) { if (groupChat.owner == player) gcList.add(groupChat) }
            // Add all chats the player is in
            else { if (groupChat.playerList.contains(player)) gcList.add(groupChat) }
        }
        return gcList
    }

    // Function that gets all the group chats that 'player' is invited to
    fun getPlayerInvitedToGCs(player: Player): List<PlayerGroupChat> = GROUP_CHATS.filter { it.invites.contains(player) }

    // Function that returns true if 'player' is owner of 'groupChat'
    fun isGCOwner(player: Player, groupChat: PlayerGroupChat): Boolean = groupChat.owner == player

    // Function that returns true if 'player' is a member of 'groupChat'
    fun isPlayerInGC(player: Player, groupChat: PlayerGroupChat): Boolean = groupChat.playerList.contains(player)

    // Function to creates a new group chat with 'owner' as its owner and 'players' as the players
    fun createGC(owner: OfflinePlayer, players: List<OfflinePlayer> = listOf(), name: String = "", staffAction: Boolean = false) : PlayerGroupChat? {
        // Check if owner has reached gc limit
        if (checkPlayerGCLimit(owner) != LimitState.VALID) return null

        // Create group chat
        val groupChat = PlayerGroupChat(id = getNewGCID(), owner = owner, name = name)
        if (name.isNotBlank()) { groupChat.prefix = "${groupChat.prefix}-$name" }
        addPlayerToGC(groupChat, players, staffAction)
        GROUP_CHATS.add(groupChat)

        // Log action
        PlayerGroupChatLog.createGC(groupChat, staffAction)

        return groupChat
    }

    // Function to deletes a group chat
    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        // Remove selected gc for members that might have it selected
        groupChat.playerList.forEach { player -> unselectPlayerGC(groupChat, player) }

        // Delete gc
        GROUP_CHATS.remove(groupChat)

        // Log action
        PlayerGroupChatLog.deleteGC(groupChat, staffAction)
    }

    // Function to add 'player' from the given 'groupChat'
    fun addPlayerToGC(groupChat: PlayerGroupChat, players: List<OfflinePlayer>, staffAction: Boolean = false) {
        players.forEach { player ->
            // Check if player has reached gc limit
            if (checkPlayerGCLimit(player) != LimitState.VALID) return@forEach

            // End if player already in gc
            if (groupChat.playerList.contains(player)) return@forEach

            // Add player (+ remove invite if there is one)
            groupChat.playerList.add(player)
            groupChat.invites.remove(player)

            // Log action
            PlayerGroupChatLog.addPlayerToGC(groupChat, player, staffAction)
        }
    }

    // Function to removes 'player' from the given 'groupChat'
    fun removePlayerFromGC(groupChat: PlayerGroupChat, players: List<OfflinePlayer>, staffAction: Boolean = false) {
        players.forEach { player ->
            // End if player not in gc
            if (!groupChat.playerList.contains(player)) return@forEach

            // Remove player
            groupChat.playerList.remove(player)

            // Remove player selected gc if they have 'groupChat' as their selected one
            unselectPlayerGC(groupChat, player)

            // Delete the GC if there are no more players
            if (groupChat.playerList.isEmpty()) { deleteGC(groupChat); return@forEach }

            // If 'player' was the owner, sets the first in 'playerList' as the new owner
            if (groupChat.owner == player) groupChat.owner = groupChat.playerList.first()

            // Log action
            PlayerGroupChatLog.removePlayerFromGC(groupChat, player, staffAction)
        }
    }

    // Function that removes player from a given amount of group chats
    fun removePlayerFromAmountOfGC(player: OfflinePlayer, amount: Int) {
        val playerGroupChats = getPlayerGCs(player)
        playerGroupChats.takeLast(amount).reversed().forEach { groupChat ->
            removePlayerFromGC(groupChat, listOf(player))
        }
    }

    // Function to invite a player to a group chat
    fun invitePlayerToGC(groupChat: PlayerGroupChat, players: List<OfflinePlayer>) {
        players.forEach { player ->
            if (groupChat.invites.contains(player)) return@forEach // Skip if already invited
            groupChat.invites.add(player)
            PlayerGroupChatLog.invitePlayerToGC(groupChat, player)
        }
    }

    // Function that adds a selected group chat for 'player' (reruns false if 'player' not in gc they are trying to select)
    fun selectPlayerGC(groupChat: PlayerGroupChat, player: OfflinePlayer): Boolean {
        if (!groupChat.playerList.contains(player)) return false
        PLAYERS_SELECTED_GC[player] = groupChat.id
        return true
    }

    // Function that unselects a group chat for 'player' if they have it selected (returns true if it was unselected)
    fun unselectPlayerGC(groupChat: PlayerGroupChat, player: OfflinePlayer): Boolean {
        val selectedGroupChat = getSelectedPlayerGC(player) ?: return false
        if (selectedGroupChat != groupChat) return false
        PLAYERS_SELECTED_GC.remove(player)
        return true
    }

    // Returns the selected group chat for the player, the only one they’re in if none is selected or null if they are in multiple but non are selected
    fun getSelectedPlayerGC(player: OfflinePlayer): PlayerGroupChat? {
        PLAYERS_SELECTED_GC[player]?.let { id -> return getGCfromID(id) }
        val playerGCs = getPlayerGCs(player)
        return if (playerGCs.size == 1) playerGCs.first() else null
    }

    // Function that checks if player is exceeding the group chat limit
    fun checkPlayerGCLimit(player: OfflinePlayer): LimitState {
        val playerGroupChats = getPlayerGCs(player)

        // If player is under the group chat limit -> return state
        if (playerGroupChats.size < GROUP_CHAT_LIMIT) return LimitState.VALID

        // If player is at the group chat limit -> return state
        else if (playerGroupChats.size == GROUP_CHAT_LIMIT) return LimitState.LIMIT

        // If player is at the group chat limit -> return state and remove player from additional group chats
        else {
            removePlayerFromAmountOfGC(player, (playerGroupChats.size - GROUP_CHAT_LIMIT))
            return LimitState.EXCEEDED
        }
    }

    // Function that takes a string with format delimiter '%gc%' and returns a component containing the string
    // Replaces '%gc%' with the group chat's name and displays the group chat's member list when hovered
    // If 'isWholeMessageHoverable' is true, the entire message will be hoverable, otherwise just the '%gc%' placeholder
    fun formatHoverableMessage(message: String, gcNameColor: String?, groupChat: PlayerGroupChat, underLined: Boolean = true, isWholeMessageHoverable: Boolean = false): Component {
        // Separates the message with '%gc%' as the delimiter
        val parts = message.split("%gc%")
        val nameColor = gcNameColor ?: "§r"

        // Build the component
        var result = Component.text("")
        for (i in parts.indices) {
            result = result.append(Component.text(parts[i]))
            if (i < parts.size - 1) {
                val prefixText = nameColor + (if (underLined) "§n" else "") + groupChat.prefix
                val prefixComponent = Component.text(prefixText).hoverEvent(getPlayerListHoverEvent(groupChat))
                result = result.append(prefixComponent)
            }
        }

        // If the whole message should be hoverable, apply the hover event to the entire component
        return if (isWholeMessageHoverable) result.hoverEvent(getPlayerListHoverEvent(groupChat)) else result
    }

    // Function that returns a hover event with the 'groupChat's' member list
    fun getPlayerListHoverEvent(groupChat: PlayerGroupChat): HoverEvent<Component> {
        var playerList = Component.text("${PREFIX}List of ${groupChat.prefix} members:\n\n")
        groupChat.playerList.forEach {
            playerList = playerList.append(Component.text(it.name!!))
            if (groupChat.owner == it) playerList = playerList.append(Component.text(" §6👑§r"))
            if (groupChat.playerList.last() != it) playerList = playerList.append(Component.text(", "))
        }
        return HoverEvent.showText(playerList)
    }

    // Function that returns a list of group chat ID-Name that 'player' is in (Used for command tab complete)
    fun tabCompletePlayerGCsList(groupChats: List<PlayerGroupChat>): List<String> {
        if (groupChats.isEmpty()) return listOf()
        return groupChats.map { gc ->
            if (gc.name.isBlank()) "${gc.id}"
            else "${gc.id}-${gc.name}"
        }
    }

    // Function that converts a tab complete group chat like "2-test" into a PlayerGroupChat object
    fun tabCompleteInputGCGet(input: String): PlayerGroupChat? {
        val groupChatID = input.split("-").firstOrNull()?.toIntOrNull() ?: return null
        return getGCfromID(groupChatID)
    }

    // Function to validate group chat name at creation, returns true if name is valid
    fun validateGCName(name: String): Boolean {
        return name.matches(Regex("^[a-zA-Z]{0,$NAME_CHARACTER_LIMIT}$"))
    }
}