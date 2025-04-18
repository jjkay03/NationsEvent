package com.jjkay03.nationsevent.group_chat_players.deprecated

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LogsManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object PlayerGroupChatUtils {

    // Function to get group chat ID of a player
    fun getGroupChatID(player: OfflinePlayer): Int {
        PlayerGroupChatManager.Companion.GROUP_CHATS.entries.forEach { (k, v) -> if (v.contains(player)) return k }
        return -1
    }

    // Function to returns the owner of a group chat
    fun getOwner(groupChatID: Int): OfflinePlayer? {
        if (!PlayerGroupChatManager.Companion.GROUP_CHATS.containsKey(groupChatID)) { return null }
        return PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.first()
    }

    // Function to create a group chat with player as owner
    fun createGroupChat(owner: OfflinePlayer, adminForce: Boolean = false): Pair<Boolean, Int> {
        if (hasGroupChat(owner) && !adminForce) return false to -1

        // Forces 'owner' to make a new group chat even if the owner is already in one
        if (adminForce) { leaveGroupChat(owner, adminForce = true) }

        // Gets the first non-used int to become the index
        var lastIndex = -1
        for (index: Int in PlayerGroupChatManager.Companion.GROUP_CHATS.keys) { if (index - lastIndex != 1) { break }; lastIndex += 1 }

        // Creation of the group chat
        PlayerGroupChatManager.Companion.GROUP_CHATS[lastIndex + 1] = mutableListOf(owner)
        PlayerGroupChatManager.Companion.INVITES[lastIndex + 1] = mutableListOf()
        PlayerGroupChatManager.Companion.STAFF_SPIES[lastIndex + 1] = mutableListOf()

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC",
            "${if (adminForce) " ${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS}" else owner.name} CREATED group chat GC${lastIndex+1}"
        )

        return true to lastIndex + 1
    }

    // Function to delete a player's group chat
    fun deleteGroupChat(owner: OfflinePlayer, adminForce: Boolean = false): Pair<Boolean, Int> {
        val groupChatID = getGroupChatID(owner)
        if (!adminForce && !isGroupChatOwner(owner)) return false to groupChatID

        sendInGroupChat(getGroupChatID(owner), "This group chat was deleted by " + if (adminForce) PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX else "the owner")

        // Deletion of the group chat
        PlayerGroupChatManager.Companion.GROUP_CHATS.remove(groupChatID)
        PlayerGroupChatManager.Companion.INVITES.remove(groupChatID)
        PlayerGroupChatManager.Companion.STAFF_SPIES.remove(groupChatID)

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC",
            "${if (adminForce) " ${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS}" else owner.name} DELETED group chat GC$groupChatID"
        )

        return true to groupChatID
    }

    // Function to get a list of all players in a group chat
    private fun getPlayerList(groupChatID: Int): MutableList<OfflinePlayer> {
        return PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID] ?: mutableListOf()
    }

    // Function to get a list of all players in a group chat
    fun getPlayerList(player: OfflinePlayer): MutableList<OfflinePlayer> {
        return getPlayerList(getGroupChatID(player))
    }

    // Function to invite a player to a group chat
    fun inviteToGroupChat(owner: Player, invited: Player) {
        // Checks
        if (hasGroupChat(invited)) { owner.sendMessage("§cThis player is already in a group chat!"); return }
        if (!isGroupChatOwner(owner)) { owner.sendMessage("§cYou are not the owner of this group chat!"); return }
        if (invited.hasPermission(Saves.PERM_STAFF) && !owner.hasPermission(Saves.PERM_STAFF)) { owner.sendMessage("§cYou can not invite staff!"); return }

        val groupChatID = getGroupChatID(owner)
        PlayerGroupChatManager.Companion.INVITES[groupChatID]!!.add(invited)

        // Notify players
        invited.sendMessage(
            Component.text("${PlayerGroupChatManager.Companion.GROUP_CHAT_COLOR}You have been invited to group chat GC§f$groupChatID by ${owner.name} §a[ACCEPT]")
                .clickEvent(ClickEvent.runCommand("/groupchat join ${owner.name}"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§aClick to join ${owner.name}'s group chat!")))
        )
        owner.sendMessage("§aInvited player ${invited.name} to your group chat")

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC",
            "${owner.name} INVITED ${invited.name} to GC${groupChatID}")
    }

    // Function to make a player join a group chat
    fun joinGroupChat(player: OfflinePlayer, inviteSender: OfflinePlayer, adminForce: Boolean = false): Pair<Boolean, Int> {
        // Check if player has invite to group
        val groupChatID = getGroupChatID(inviteSender)
        if (!PlayerGroupChatManager.Companion.INVITES[groupChatID]!!.contains(player) && !adminForce) { return false to groupChatID }

        // Forces 'player' to join the group chat owned by 'inviteSender' even if 'player' is already in one
        if (adminForce) { leaveGroupChat(player, adminForce = true) }

        // Add player to group and remove from invites list
        PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.add(player)
        PlayerGroupChatManager.Companion.INVITES[groupChatID]!!.remove(player)

        // Notify players in group of who joined
        sendInGroupChat(groupChatID, if (adminForce) "${player.name} was put into this group chat by Staff" else "${player.name} joined this group chat")

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC",
            if (adminForce) "${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS} FORCIBLY ADDED ${player.name} to ${inviteSender.name}'s group GC${groupChatID}"
            else "${player.name} JOINED ${inviteSender.name}'s group GC${groupChatID}"
        )

        return true to getGroupChatID(player)
    }

    // Function to remove a player from a group chat
    fun leaveGroupChat(player: OfflinePlayer, kicker: OfflinePlayer? = null, adminForce: Boolean = false): Pair<Boolean, Int> {
        // Checks
        if (!hasGroupChat(player)) return false to -1
        if (kicker != null && !isGroupChatOwner(kicker)) return false to getGroupChatID(kicker)

        // Get group chat
        val groupChatID = getGroupChatID(kicker ?: player)
        if (!isInGroupChat(groupChatID, player)) return false to -1

        // Remove player from group chat. Delete if the player is the last member
        if (PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.size == 1) deleteGroupChat(player)
        else PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.remove(player)

        // Notify players in group chat of the player that left
        sendInGroupChat(groupChatID, if (adminForce) "${player.name} was removed from this group chat by Staff" else "${player.name} left this group chat")

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC",
            if (adminForce || kicker != null) "${if (adminForce) "${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS} FORCIBLY" else kicker!!.name} KICKED ${player.name} from GC$groupChatID"
            else "${player.name} LEFT from GC${groupChatID}"
        )

        return true to groupChatID
    }

    // Function to send a message to all player in a group chat
    fun sendInGroupChat(groupChatID: Int, message: String, isStaff: Boolean = false) {
        if (!hasOnlinePlayers(groupChatID)) return

        // Send message to all players in group chat
        PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.filter { it.isOnline }.forEach { it.player!!.sendMessage(
            Component.text()
                .append(buildGroupChatMessagePrefix(groupChatID, PlayerGroupChatManager.Companion.GROUP_CHAT_COLOR!!))
                .append(Component.text("${if (isStaff) " ${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX}" else ""}${PlayerGroupChatManager.Companion.GROUP_CHAT_COLOR} $message"))
        ) }

        // Send message to staff spies spying on this specific group chat
        PlayerGroupChatManager.Companion.STAFF_SPIES[groupChatID]!!.filter { it.isOnline && !isInGroupChat(groupChatID, it) }.forEach { it.player!!.sendMessage(
            Component.text()
                .append(buildGroupChatMessagePrefix(groupChatID, PlayerGroupChatManager.Companion.GROUP_CHAT_SPY_COLOR!!))
                .append(Component.text("${if (isStaff) " ${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX}" else ""}${PlayerGroupChatManager.Companion.GROUP_CHAT_SPY_COLOR} $message"))
        ) }

        // Send message to staff spies spying on ALL group chats
        PlayerGroupChatManager.Companion.STAFF_SPIES[-1]!!.filter { it.isOnline && !isInGroupChat(groupChatID, it) && !PlayerGroupChatManager.Companion.STAFF_SPIES[groupChatID]!!.contains(it) }.forEach { it.player!!.sendMessage(
            Component.text()
                .append(buildGroupChatMessagePrefix(groupChatID, PlayerGroupChatManager.Companion.GROUP_CHAT_SPY_COLOR!!))
                .append(Component.text("${if (isStaff) " ${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX}" else ""}${PlayerGroupChatManager.Companion.GROUP_CHAT_SPY_COLOR} $message"))
        ) }

        // Log message to log file
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "[CHAT] [GC$groupChatID] $message")
    }

    // Function to send a message to all player in a group chat
    fun sendInGroupChat(player: Player, message: String) {
        if (!hasGroupChat(player)) { player.sendMessage("§cYou are not in a group chat!"); return }
        if (!PlayerGroupChatManager.Companion.BYPASS_DISABLED_CHAT && !player.hasPermission(Saves.PERM_USE_CHAT)) { player.sendMessage("§cChat is disabled!"); return }
        sendInGroupChat(getGroupChatID(player), "${player.name}: $message")
    }

    // Function to change owner of a group chat
    fun setOwner(previousOwner: OfflinePlayer, newOwner: OfflinePlayer, adminForce: Boolean = false): Pair<Boolean, Int> {
        // Check
        if (!hasGroupChat(previousOwner)) return false to -1
        if (!isGroupChatOwner(previousOwner) && !adminForce) return true to -1
        val groupChatID = getGroupChatID(previousOwner)
        if (!isInGroupChat(groupChatID, newOwner)) {
            if (!adminForce) return false to groupChatID // If not forced by admin, return as failed
            else { joinGroupChat(newOwner, previousOwner, true) } // If newOwner is not in previousOwner's GC, force join
        }

        // Switch group chat owner by moving the new owner to the first position of the list
        val index = PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.indexOf(newOwner)
        PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!![index] = PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!![0]
        PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!![0] = newOwner

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC",
            "${if (adminForce) "${PlayerGroupChatManager.Companion.STAFF_MESSAGE_PREFIX_FORMATLESS} FORCIBLY" else previousOwner.name} TRANSFERRED GC$groupChatID to ${newOwner.name}"
        )

        return true to groupChatID
    }

    // Function to plant a spy ඞ
    fun setSpy(player: Player, groupChatID: Int = -1): Pair<Boolean, Int> {
        if (!isGroupChat(groupChatID) && groupChatID != -1) return false to groupChatID

        // Adds the spy to the spy map
        if (!PlayerGroupChatManager.Companion.STAFF_SPIES.containsKey(groupChatID)) PlayerGroupChatManager.Companion.STAFF_SPIES[groupChatID] = mutableListOf(player)
        else PlayerGroupChatManager.Companion.STAFF_SPIES[groupChatID]!!.add(player)

        // Log action
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "${player.name} is SPYING in GC$groupChatID")

        return true to groupChatID
    }

    // Function to get invites of a player
    fun getInvites(player: OfflinePlayer): List<OfflinePlayer> {
        return PlayerGroupChatManager.Companion.INVITES.filter { it.value.contains(player) }.values.map { it.first() }
    }

    // Function to check if a player is a group chat in general
    fun hasGroupChat(player: OfflinePlayer): Boolean {
        return getGroupChatID(player) != -1
    }

    // Function to check if player is in a specific group chat
    private fun isInGroupChat(groupChatID: Int, player: OfflinePlayer): Boolean {
        return isGroupChat(groupChatID) && getGroupChatID(player) == groupChatID
    }

    // Function to check if a player is group chat owner
    fun isGroupChatOwner(player: OfflinePlayer): Boolean {
        if (!hasGroupChat(player)) return false
        return PlayerGroupChatManager.Companion.GROUP_CHATS[getGroupChatID(player)]!![0] == player
    }

    // Function that check if an ID has a group chat associated with it
    fun isGroupChat(groupChatID: Int): Boolean {
        return PlayerGroupChatManager.Companion.GROUP_CHATS.containsKey(groupChatID)
    }

    // Function that checks if a group chat has any online players
    private fun hasOnlinePlayers(groupChatID: Int): Boolean {
        if (!isGroupChat(groupChatID)) return false
        PlayerGroupChatManager.Companion.GROUP_CHATS[groupChatID]!!.forEach { if (it.isOnline) return true }
        return false
    }

    // Function that creates/build the group chat message prefix
    fun buildGroupChatMessagePrefix(groupChatID: Int, color: String): Component {
        return Component.text("$color[GC$groupChatID]")
            .hoverEvent(createGroupChatMessageHover(groupChatID))
    }

    // Function that creates hover message for group chat message prefix
    fun createGroupChatMessageHover(groupChatID: Int): HoverEvent<Component> {
        val text = StringBuilder("§a§nGC$groupChatID Member List:§r\n")
        for (p: OfflinePlayer in getPlayerList(groupChatID)) {
            text.append(p.name!!)
            if (isGroupChatOwner(p)) text.append(" §6👑§r")
            if (p != getPlayerList(groupChatID).last()) text.append(", ")
        }
        return HoverEvent.showText(Component.text(text.toString()))
    }
}