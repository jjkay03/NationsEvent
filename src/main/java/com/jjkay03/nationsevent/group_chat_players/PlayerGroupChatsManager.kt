package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.FilesManager
import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

object PlayerGroupChatsManager {

    init { loadGroupChats() }

    private val ENABLED = NationsEvent.INSTANCE.config.getBoolean("player-group-chats-enable")
    private val GROUPCHATCOLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-color")
    private val GROUPCHATSPYCOLOR = NationsEvent.INSTANCE.config.getString("player-group-chat-spy-color")
    private val BYPASSDISABLEDCHAT = NationsEvent.INSTANCE.config.getBoolean("player-group-chat-bypass-disabled-chat")

    private val GROUP_CHATS = mutableMapOf<Int, MutableList<OfflinePlayer>>()
    private val INVITES = mutableMapOf<Int, MutableList<Player>>()
    private val STAFF_SPIES = mutableListOf<Player>()

    private fun loadGroupChats() {
        if (!ENABLED) return

        FilesManager.createFile(Saves.FILE_PLAYER_GROUP_CHATS)

        NationsEvent.INSTANCE.getCommand("groupchat")?.apply { setExecutor(PlayerGroupChatsCommand()) }
        NationsEvent.INSTANCE.getCommand("admingroupchat")?.apply { setExecutor(AdminGroupChatsCommand()) }

        loadGroupChatsFromFile(Saves.FILE_PLAYER_GROUP_CHATS)
    }

    private fun loadGroupChatsFromFile(file: File) {
        YamlConfiguration.loadConfiguration(file).getValues(true).forEach {
            (p, v) -> run {
                if (v.javaClass != listOf("").javaClass) return@run
                GROUP_CHATS.plus(p to (v as List<*>).map { n -> Bukkit.getOfflinePlayer(UUID.fromString(n.toString())) })
            }
        }
    }

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
        return true to GROUP_CHATS.keys.size
    }

    fun deleteGroupChat(owner: Player, bypassOwner: Boolean = false): Pair<Boolean, Int> {
        val groupchatID = getGroupChatID(owner)
        if (!bypassOwner || !isGroupChatOwner(owner)) return false to groupchatID

        GROUP_CHATS.remove(groupchatID)
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

        if (INVITES.containsKey(groupchatID)) { INVITES[groupchatID]!!.plus(invited) }
        else { INVITES.plus(groupchatID to mutableListOf(invited)) }

        invited.sendMessage(
            Component.text("${GROUPCHATCOLOR}You have been invited to group chat §f${getGroupChatName(groupchatID)} by ${owner.name} §a[ACCEPT]")
                .clickEvent(ClickEvent.runCommand("/groupchat join ${owner.name}"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("§aClick to join ${owner.name}'s group chat!")))
        )

        owner.sendMessage("§aInvited player ${invited.name} to your group chat")
    }

    fun joinGroupChat(player: OfflinePlayer, inviteSender: OfflinePlayer): Pair<Boolean, Int> {
        val groupchatID = getGroupChatID(inviteSender)
        if (!INVITES.containsKey(groupchatID) || !INVITES[groupchatID]!!.contains(player)) { return false to -1 }

        GROUP_CHATS[groupchatID]!!.plus(player)

        INVITES[groupchatID]!!.minus(player)
        if (INVITES[groupchatID]!!.isEmpty()) INVITES.remove(groupchatID)

        sendInGroupChat(groupchatID, "${player.name} joined this group chat")

        return true to getGroupChatID(player)
    }

    fun leaveGroupChat(player: OfflinePlayer, kicker: OfflinePlayer? = null): Pair<Boolean, Int> {
        if (!isInAGroupChat(player)) return false to -1
        if (kicker != null && !isGroupChatOwner(kicker)) return false to getGroupChatID(kicker)

        val groupchatID = getGroupChatID(kicker ?: player)
        if (!isInGroupChat(groupchatID, player)) return false to -1

        GROUP_CHATS[groupchatID]!!.minus(player)
        if (GROUP_CHATS[groupchatID]!!.isEmpty()) GROUP_CHATS.remove(groupchatID)

        sendInGroupChat(groupchatID, "${player.name} left this group chat")

        return true to groupchatID
    }

    fun sendInGroupChat(groupchatID: Int, message: String) {
        if (!hasOnlinePlayers(groupchatID)) return

        GROUP_CHATS[groupchatID]!!.filter { p -> p.isOnline }.forEach { p -> p.player!!.sendMessage("$GROUPCHATCOLOR[${getGroupChatName(groupchatID)}] $message") }
        STAFF_SPIES.filter { p -> p.isOnline }.forEach { p -> p.player!!.sendMessage("$GROUPCHATSPYCOLOR[${getGroupChatName(groupchatID)}] $message") }
    }

    fun sendInGroupChat(player: Player, message: String) {
        if (!isInAGroupChat(player)) { player.sendMessage("§cYou are not in a group chat!"); return }
        if (!BYPASSDISABLEDCHAT && !player.hasPermission(Saves.PERM_USE_CHAT)) { player.sendMessage("§cChat is disabled!"); return }

        sendInGroupChat(getGroupChatID(player), "${player.name} $message")
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
        GROUP_CHATS[groupchatID]!!.forEach { p -> if (p.isOnline) return true }
        return false
    }
}