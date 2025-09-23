package com.jjkay03.nationsevent.chat.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.OfflinePlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.entity.Player
import kotlin.collections.set

object PlayerGroupChatUtils {

    // Use to signify the state of a player group chats limit
    enum class LimitState { VALID, LIMIT, EXCEEDED }

    // Function used to send message in a group chat
    fun chat(groupChat: PlayerGroupChat, player: Player?, message: String, staffAction: Boolean = false) {
        // Player is unable to chat in GCs if they do not have the 'PERM_USE_CHAT' permission UNLESS 'BYPASS_DISABLED_CHAT' is true
        if (player != null && !player.hasPermission(Saves.PERM_USE_CHAT) && !Config.PGC_BYPASS_DISABLED_CHAT) { player.sendMessage("§cChat is disabled!"); return }

        // Message components
        val prefix = "${Config.PGC_COLOR}[%gc%${Config.PGC_COLOR}] "
        val spyPrefix = "${Config.PGC_SPY_COLOR}[%gc%${Config.PGC_SPY_COLOR}] "

        // Author formatting helper
        fun formatAuthor(color: String?): String = when {
            player == null -> ""
            staffAction -> "${Config.PGC_STAFF_MESSAGE_PREFIX}$color ${player.name}: "
            else -> "${player.name}: "
        }

        // Create messages
        val playerMsg = formatHoverableMessage(prefix + formatAuthor(Config.PGC_COLOR) + message,
            Config.PGC_COLOR, groupChat, false)
        val spiesMsg = formatHoverableMessage(spyPrefix + formatAuthor(Config.PGC_SPY_COLOR) + message,
            Config.PGC_SPY_COLOR, groupChat, false)

        // Send messages to group members
        val groupMembers = groupChat.playerList.mapNotNull { it.player }
        val sentTo = groupMembers.mapTo(mutableSetOf()) { it.uniqueId }
        groupMembers.forEach { it.sendMessage(playerMsg) }

        // Send to spies if they have not received yet
        (groupChat.spies + PlayerGroupChatManager.GLOBAL_SPIES)
            .mapNotNull { it.player }
            .distinctBy { it.uniqueId }
            .filterNot { it.uniqueId in sentTo }
            .forEach { it.sendMessage(spiesMsg) }

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
        PlayerGroupChatManager.GROUP_CHATS.forEach { ids.add(it.id) }
        ids.sort()

        // Gets the first non-used int to become the index
        var lastIndex = -1
        for (index in ids) { if (index - lastIndex != 1) { break }; lastIndex += 1 }

        // Returns the smallest available Int
        return lastIndex + 1
    }

    // Function that looks for a group chat using an ID
    fun getGCfromID(id: Int): PlayerGroupChat? {
        for (groupChat in PlayerGroupChatManager.GROUP_CHATS) { if (groupChat.id == id) return groupChat }
        return null // Return null if no group chat with 'id' found
    }

    // Function that gets all the group chats 'player' is in
    fun getPlayerGCs(player: OfflinePlayer, ownedGroupChatOnly: Boolean = false): List<PlayerGroupChat> {
        val gcList = mutableListOf<PlayerGroupChat>()
        PlayerGroupChatManager.GROUP_CHATS.forEach { groupChat ->
            // Only check ownership when ownedGroupChatOnly is true
            if (ownedGroupChatOnly) { if (groupChat.owner == player) gcList.add(groupChat) }
            // Add all chats the player is in
            else { if (groupChat.playerList.contains(player)) gcList.add(groupChat) }
        }
        return gcList
    }

    // Function that gets all the group chats that 'player' is invited to
    fun getPlayerInvitedToGCs(player: Player): List<PlayerGroupChat> = PlayerGroupChatManager.GROUP_CHATS.filter { it.invites.contains(player) }

    // Function that returns true if 'player' is a member of 'groupChat'
    fun isPlayerInGC(player: Player, groupChat: PlayerGroupChat): Boolean = groupChat.playerList.contains(player)

    // Function to change owner of a group
    fun setGCOwner(groupChat: PlayerGroupChat, newOwner: OfflinePlayer) { groupChat.owner = newOwner }

    // Function to add a spy to a group (returns true when spy is added, false is spy already in group)
    fun spyAdd(groupChat: PlayerGroupChat, spy: OfflinePlayer): Boolean {
        if (!groupChat.spies.contains(spy)) { groupChat.spies.add(spy); return true }
        else return false
    }

    // Function to remove spy from a group
    fun spyRemove(groupChat: PlayerGroupChat, spy: OfflinePlayer) { groupChat.spies.remove(spy) }

    // Function to remove spy from all groups and global
    fun spyRemoveAll(spy: OfflinePlayer) {
        PlayerGroupChatManager.GLOBAL_SPIES.remove(spy)
        for (group in PlayerGroupChatManager.GROUP_CHATS) {
            group.spies.remove(spy)
        }
    }

    // Function to creates a new group chat with 'owner' as its owner and 'players' as the players
    fun createGC(owner: OfflinePlayer, players: List<OfflinePlayer> = listOf(), name: String = "", staffAction: Boolean = false) : PlayerGroupChat? {
        // Check if owner has reached gc limit
        if (checkPlayerGCLimit(owner) != LimitState.VALID) return null

        // Create group chat
        val groupChat = PlayerGroupChat(id = getNewGCID(), owner = owner, name = name)
        if (name.isNotBlank()) { groupChat.prefix = "${groupChat.prefix}-$name" }
        addPlayerToGC(groupChat, players, staffAction)
        PlayerGroupChatManager.GROUP_CHATS.add(groupChat)

        // Log action
        PlayerGroupChatLog.createGC(groupChat, staffAction)

        return groupChat
    }

    // Function to deletes a group chat
    fun deleteGC(groupChat: PlayerGroupChat, staffAction: Boolean = false) {
        // Remove selected gc for members that might have it selected
        groupChat.playerList.forEach { player -> unselectPlayerGC(groupChat, player) }

        // Delete gc
        PlayerGroupChatManager.GROUP_CHATS.remove(groupChat)

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

    // Function that adds a selected group chat for 'player' (reruns false if 'player' not in gc they are trying to select)
    fun selectPlayerGC(groupChat: PlayerGroupChat, player: OfflinePlayer): Boolean {
        if (!groupChat.playerList.contains(player)) return false
        PlayerGroupChatManager.PLAYERS_SELECTED_GC[player] = groupChat.id
        return true
    }

    // Function that unselects a group chat for 'player' if they have it selected (returns true if it was unselected)
    fun unselectPlayerGC(groupChat: PlayerGroupChat, player: OfflinePlayer): Boolean {
        val selectedGroupChat = getSelectedPlayerGC(player) ?: return false
        if (selectedGroupChat != groupChat) return false
        PlayerGroupChatManager.PLAYERS_SELECTED_GC.remove(player)
        return true
    }

    // Returns the selected group chat for the player, the only one they’re in if none is selected or null if they are in multiple but non are selected
    fun getSelectedPlayerGC(player: OfflinePlayer): PlayerGroupChat? {
        PlayerGroupChatManager.PLAYERS_SELECTED_GC[player]?.let { id -> return getGCfromID(id) }
        val playerGCs = getPlayerGCs(player)
        return if (playerGCs.size == 1) playerGCs.first() else null
    }

    // Function that checks if player is exceeding the group chat limit
    fun checkPlayerGCLimit(player: OfflinePlayer): LimitState {
        // End if player is staff (bypass)
        if ((player as? Player)?.hasPermission(PlayerGroupChatManager.PERM_BYPASS_LIMIT) == true) return LimitState.VALID

        val playerGroupChats = getPlayerGCs(player)

        // If player is under the group chat limit -> return state
        if (playerGroupChats.size < Config.PGC_LIMIT) return LimitState.VALID

        // If player is at the group chat limit -> return state
        else if (playerGroupChats.size == Config.PGC_LIMIT) return LimitState.LIMIT

        // If player is at the group chat limit -> return state and remove player from additional group chats
        else {
            removePlayerFromAmountOfGC(player, (playerGroupChats.size - Config.PGC_LIMIT))
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
        var playerList = Component.text("👥 ${groupChat.prefix} members:\n\n")
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
        return name.matches(Regex("^[a-zA-Z]{0,${Config.PGC_NAME_CHARACTER_LIMIT}}$"))
    }
}