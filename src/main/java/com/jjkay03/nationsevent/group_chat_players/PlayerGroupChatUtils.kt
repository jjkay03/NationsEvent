package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.BYPASS_DISABLED_CHAT
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHAT_COLOR
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHAT_SPY_COLOR
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.INVITES
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.STAFF_SPIES
import com.jjkay03.nationsevent.utils.LogsManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object PlayerGroupChatUtils {

    // Function to get group chat ID of a player
    fun getGroupChatID(player: OfflinePlayer): Int {
        GROUP_CHATS.entries.forEach { (k, v) -> if (v.contains(player)) return k }
        return -1
    }

    // Function to get a group chat name from ID (Example: GC1)
    fun getGroupChatName(groupChatID: Int): String {
        return "GC$groupChatID"
    }

    // Function to create a group chat with player as owner
    fun createGroupChat(owner: OfflinePlayer, adminForce: Boolean = false): Pair<Boolean, Int> {
        if (hasGroupChat(owner) && !adminForce) return false to -1

        // Forces 'owner' to make a new group chat even if the owner is already in one
        if (adminForce) { leaveGroupChat(owner, adminForce = true) }

        GROUP_CHATS[GROUP_CHATS.keys.size] = mutableListOf(owner)
        return true to GROUP_CHATS.keys.size - 1
    }

    // Function to delete a player's group chat
    fun deleteGroupChat(owner: OfflinePlayer, bypassOwner: Boolean = false): Pair<Boolean, Int> {
        val groupChatID = getGroupChatID(owner)
        if (!bypassOwner && !isGroupChatOwner(owner)) return false to groupChatID

        sendInGroupChat(getGroupChatID(owner), "This group chat was deleted by the owner")

        GROUP_CHATS.remove(groupChatID)
        INVITES.remove(groupChatID)
        return true to groupChatID
    }

    // Function to get a list of all players in a group chat
    private fun getPlayerList(groupChatID: Int): MutableList<OfflinePlayer> {
        return GROUP_CHATS[groupChatID] ?: mutableListOf()
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

        // Add player to invited players list
        if (!INVITES.containsKey(groupChatID)) { INVITES[groupChatID] = mutableListOf(invited) }
        else { INVITES[groupChatID]!!.add(invited) }

        // Notify players
        invited.sendMessage(
            Component.text("${GROUP_CHAT_COLOR}You have been invited to group chat §f${getGroupChatName(groupChatID)} by ${owner.name} §a[ACCEPT]")
                .clickEvent(ClickEvent.runCommand("/groupchat join ${owner.name}"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§aClick to join ${owner.name}'s group chat!")))
        )
        owner.sendMessage("§aInvited player ${invited.name} to your group chat")
    }

    // Function to make a player join a group chat
    fun joinGroupChat(player: OfflinePlayer, inviteSender: OfflinePlayer, adminForce: Boolean = false): Pair<Boolean, Int> {
        // Check if player has invite to group
        val groupChatID = getGroupChatID(inviteSender)
        if ((!INVITES.containsKey(groupChatID) || !INVITES[groupChatID]!!.contains(player)) && !adminForce) { return false to groupChatID }

        // Forces 'player' to join the group chat owned by 'inviteSender' even if 'player' is already in one
        if (adminForce) { leaveGroupChat(player, adminForce = true) }

        // Add player to group and remove from invites list
        GROUP_CHATS[groupChatID]!!.add(player)
        INVITES[groupChatID]!!.remove(player)
        if (INVITES[groupChatID]!!.isEmpty()) INVITES.remove(groupChatID)

        // Notify players in group of who joined
        sendInGroupChat(groupChatID, "${player.name} joined this group chat")

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

        // Remove player from group chat
        GROUP_CHATS[groupChatID]!!.remove(player)
        if (GROUP_CHATS[groupChatID]!!.isEmpty()) GROUP_CHATS.remove(groupChatID)

        // Notify players in group chat of the player that left
        sendInGroupChat(groupChatID, if (adminForce) "${player.name} was removed from this group chat by an admin" else "${player.name} left this group chat")

        return true to groupChatID
    }

    // Function to send a message to all player in a group chat
    fun sendInGroupChat(groupChatID: Int, message: String) {
        if (!hasOnlinePlayers(groupChatID)) return

        // Send message to all players in group chat
        GROUP_CHATS[groupChatID]!!.filter { it.isOnline }.forEach { it.player!!.sendMessage(
            Component.text().append(buildGroupChatMessagePrefix(groupChatID, GROUP_CHAT_COLOR!!)).append(Component.text("$GROUP_CHAT_COLOR $message"))
        ) }

        // Send message to all staff spies
        STAFF_SPIES.filter { it.isOnline && !isInGroupChat(groupChatID, it) }.forEach { it.player!!.sendMessage(
            Component.text().append(buildGroupChatMessagePrefix(groupChatID, GROUP_CHAT_SPY_COLOR!!)).append(Component.text("$GROUP_CHAT_SPY_COLOR $message"))
        ) }

        // Log message to log file
        LogsManager.log(Saves.LOG_FILE_PLAYER_GC, "Player GC", "[CHAT] [${getGroupChatName(groupChatID)}] $message")
    }

    // Function to send a message to all player in a group chat
    fun sendInGroupChat(player: Player, message: String) {
        if (!hasGroupChat(player)) { player.sendMessage("§cYou are not in a group chat!"); return }
        if (!BYPASS_DISABLED_CHAT && !player.hasPermission(Saves.PERM_USE_CHAT)) { player.sendMessage("§cChat is disabled!"); return }
        sendInGroupChat(getGroupChatID(player), "${player.name}: $message")
    }

    // Function to change owner of a group chat
    fun setOwner(previousOwner: OfflinePlayer, newOwner: OfflinePlayer): Pair<Boolean, Int> {
        // Check
        if (!hasGroupChat(previousOwner)) return false to -1
        if (!isGroupChatOwner(previousOwner)) return true to -1
        val groupChatID = getGroupChatID(previousOwner)
        if (!isInGroupChat(groupChatID, newOwner)) return false to groupChatID

        // Switch group chat owner by moving the new owner to the first position of the list
        val index = GROUP_CHATS[groupChatID]!!.indexOf(newOwner)
        GROUP_CHATS[groupChatID]!![index] = GROUP_CHATS[groupChatID]!![0]
        GROUP_CHATS[groupChatID]!![0] = newOwner
        return true to groupChatID
    }

    // Function to get invites of a player
    fun getInvites(player: OfflinePlayer): List<OfflinePlayer> {
        return INVITES.filter { it.value.contains(player) }.values.map { it.first() }
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
        return GROUP_CHATS[getGroupChatID(player)]!![0] == player
    }

    // Function that check if an ID has a group chat associated with it
    fun isGroupChat(groupChatID: Int): Boolean {
        return GROUP_CHATS.containsKey(groupChatID)
    }

    // Function that checks if a group chat has any online players
    private fun hasOnlinePlayers(groupChatID: Int): Boolean {
        if (!isGroupChat(groupChatID)) return false
        GROUP_CHATS[groupChatID]!!.forEach { if (it.isOnline) return true }
        return false
    }

    // Function that creates/build the group chat message prefix
    private fun buildGroupChatMessagePrefix(groupChatID: Int, color: String): Component {
        return Component.text("$color[${getGroupChatName(groupChatID)}]")
            .hoverEvent(createGroupChatMessageHover(getGroupChatName(groupChatID), groupChatID))
    }

    // Function that creates hover message for group chat message prefix
    fun createGroupChatMessageHover(groupName: String, groupChatID: Int): HoverEvent<Component> {
        val text = StringBuilder("§a§n$groupName Member List:§r\n")
        for (p: OfflinePlayer in getPlayerList(groupChatID)) {
            text.append(p.name!!)
            if (isGroupChatOwner(p)) text.append(" §6👑§r")
            if (p != getPlayerList(groupChatID).last()) text.append(", ")
        }
        return HoverEvent.showText(Component.text(text.toString()))
    }
}