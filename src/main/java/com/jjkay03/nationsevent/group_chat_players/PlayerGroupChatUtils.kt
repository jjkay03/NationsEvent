package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.BYPASSDISABLEDCHAT
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUPCHATCOLOR
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUPCHATSPYCOLOR
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.INVITES
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.STAFF_SPIES
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object PlayerGroupChatUtils {

    fun getGroupChatID(player: OfflinePlayer): Int {
        GROUP_CHATS.entries.forEach { (k, v) -> if (v.contains(player)) return k }
        return -1
    }

    fun getGroupChatName(groupchatID: Int): String {
        return "GC$groupchatID"
    }

    fun createGroupChat(owner: Player): Pair<Boolean, Int> {
        if (isInAGroupChat(owner)) return false to -1

        GROUP_CHATS[GROUP_CHATS.keys.size] = mutableListOf(owner)
        return true to GROUP_CHATS.keys.size - 1
    }

    fun deleteGroupChat(owner: Player, bypassOwner: Boolean = false): Pair<Boolean, Int> {
        val groupchatID = getGroupChatID(owner)
        if (!bypassOwner && !isGroupChatOwner(owner)) return false to groupchatID

        sendInGroupChat(owner, "This group chat was deleted by the owner")

        GROUP_CHATS.remove(groupchatID)
        INVITES.remove(groupchatID)
        return true to groupchatID
    }

    fun getPlayerList(groupchatID: Int): MutableList<OfflinePlayer> {
        return GROUP_CHATS[groupchatID] ?: mutableListOf()
    }

    fun getPlayerList(player: OfflinePlayer): MutableList<OfflinePlayer> {
        return getPlayerList(getGroupChatID(player))
    }

    fun inviteToGroupChat(owner: Player, invited: Player) {
        if (isInAGroupChat(invited)) { owner.sendMessage("§cThis player is already in a group chat!"); return }
        if (!isGroupChatOwner(owner)) { owner.sendMessage("§cYou are not the owner of this group chat!"); return }
        if (invited.hasPermission(Saves.PERM_STAFF) && !owner.hasPermission(Saves.PERM_STAFF)) { owner.sendMessage("§cYou can not invite staff!"); return }

        val groupchatID = getGroupChatID(owner)

        if (!INVITES.containsKey(groupchatID)) { INVITES[groupchatID] = mutableListOf(invited) }
        else { INVITES[groupchatID]!!.add(invited) }

        invited.sendMessage(
            Component.text("${GROUPCHATCOLOR}You have been invited to group chat §f${getGroupChatName(groupchatID)} by ${owner.name} §a[ACCEPT]")
                .clickEvent(ClickEvent.runCommand("/groupchat join ${owner.name}"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§aClick to join ${owner.name}'s group chat!")))
        )

        owner.sendMessage("§aInvited player ${invited.name} to your group chat")
    }

    fun joinGroupChat(player: OfflinePlayer, inviteSender: OfflinePlayer): Pair<Boolean, Int> {
        val groupchatID = getGroupChatID(inviteSender)
        if (!INVITES.containsKey(groupchatID) || !INVITES[groupchatID]!!.contains(player)) { return false to groupchatID }

        GROUP_CHATS[groupchatID]!!.add(player)

        INVITES[groupchatID]!!.remove(player)
        if (INVITES[groupchatID]!!.isEmpty()) INVITES.remove(groupchatID)

        sendInGroupChat(groupchatID, "${player.name} joined this group chat")

        return true to getGroupChatID(player)
    }

    fun leaveGroupChat(player: OfflinePlayer, kicker: OfflinePlayer? = null): Pair<Boolean, Int> {
        if (!isInAGroupChat(player)) return false to -1
        if (kicker != null && !isGroupChatOwner(kicker)) return false to getGroupChatID(kicker)

        val groupchatID = getGroupChatID(kicker ?: player)
        if (!isInGroupChat(groupchatID, player)) return false to -1

        GROUP_CHATS[groupchatID]!!.remove(player)
        if (GROUP_CHATS[groupchatID]!!.isEmpty()) GROUP_CHATS.remove(groupchatID)

        sendInGroupChat(groupchatID, "${player.name} left this group chat")

        return true to groupchatID
    }

    fun sendInGroupChat(groupchatID: Int, message: String) {
        if (!hasOnlinePlayers(groupchatID)) return

        GROUP_CHATS[groupchatID]!!.filter { it.isOnline }.forEach { it.player!!.sendMessage(
            Component.text().append(buildGroupChatMessagePrefix(groupchatID, GROUPCHATCOLOR!!)).append(Component.text("$GROUPCHATCOLOR $message"))
        ) }
        STAFF_SPIES.filter { it.isOnline && !isInGroupChat(groupchatID, it) }.forEach { it.player!!.sendMessage(
            Component.text().append(buildGroupChatMessagePrefix(groupchatID, GROUPCHATSPYCOLOR!!)).append(Component.text("$GROUPCHATSPYCOLOR $message"))
        ) }
    }

    fun sendInGroupChat(player: Player, message: String) {
        if (!isInAGroupChat(player)) { player.sendMessage("§cYou are not in a group chat!"); return }
        if (!BYPASSDISABLEDCHAT && !player.hasPermission(Saves.PERM_USE_CHAT)) { player.sendMessage("§cChat is disabled!"); return }

        sendInGroupChat(getGroupChatID(player), "${player.name}: $message")
    }

    fun setOwner(previousOwner: Player, newOwner: OfflinePlayer): Pair<Boolean, Int> {
        if (!isInAGroupChat(previousOwner)) return false to -1
        if (!isGroupChatOwner(previousOwner)) return true to -1

        val groupchatID = getGroupChatID(previousOwner)
        if (!isInGroupChat(groupchatID, newOwner)) return false to groupchatID

        val index = GROUP_CHATS[groupchatID]!!.indexOf(newOwner)
        GROUP_CHATS[groupchatID]!![index] = GROUP_CHATS[groupchatID]!![0]
        GROUP_CHATS[groupchatID]!![0] = newOwner
        return true to groupchatID
    }

    fun getInvites(player: OfflinePlayer): List<OfflinePlayer> {
        return INVITES.filter { it.value.contains(player) }.values.map { it.first() }
    }

    fun isInAGroupChat(player: OfflinePlayer): Boolean {
        return getGroupChatID(player) != -1
    }

    fun isInGroupChat(groupchatID: Int, player: OfflinePlayer): Boolean {
        return isGroupChat(groupchatID) && GROUP_CHATS[groupchatID]!!.contains(player)
    }

    fun isGroupChatOwner(player: OfflinePlayer): Boolean {
        if (!isInAGroupChat(player)) return false
        return GROUP_CHATS[getGroupChatID(player)]!![0] == player
    }

    fun isGroupChat(groupchatID: Int): Boolean {
        return GROUP_CHATS.containsKey(groupchatID)
    }

    fun hasOnlinePlayers(groupchatID: Int): Boolean {
        if (!isGroupChat(groupchatID)) return false
        GROUP_CHATS[groupchatID]!!.forEach { if (it.isOnline) return true }
        return false
    }

    fun buildGroupChatMessagePrefix(groupchatID: Int, color: String): Component {
        return Component.text("$color[${getGroupChatName(groupchatID)}]")
            .hoverEvent(createGroupChatMessageHover(getGroupChatName(groupchatID), groupchatID))
    }

    fun createGroupChatMessageHover(groupName: String, groupchatID: Int): HoverEvent<Component> {
        val text = StringBuilder("§a§n$groupName Member List:§r\n")
        for (p: OfflinePlayer in getPlayerList(groupchatID)) {
            text.append(p.name!!)
            if (isGroupChatOwner(p)) text.append(" §6👑§r")
            if (p != getPlayerList(groupchatID).last()) text.append(", ")
        }
        return HoverEvent.showText(Component.text(text.toString()))
    }
}