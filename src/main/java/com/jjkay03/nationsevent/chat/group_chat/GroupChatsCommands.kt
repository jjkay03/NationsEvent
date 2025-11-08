package com.jjkay03.nationsevent.chat.group_chat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.concurrent.ConcurrentHashMap

class GroupChatsCommands : CommandExecutor, Listener {

    companion object {
        // List of toggled chats players
        val TOGGLED_GROUP_CHATS = ConcurrentHashMap<Player, GroupChats>()
    }

    // INITIALIZATION
    init {
        registerCommands()
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading Group Chats (${GroupChats.entries.size})")
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command"); return true }
        val player = sender

        // Find which group chat this command belongs to
        val groupChat = GroupChats.entries.find { it.command.contains(cmd.name.lowercase()) }
            ?: return false // Command not found

        // End if player doesn't have send perm or staff perm
        if (!player.hasPermission(groupChat.permissionSend) && !player.hasPermission(Saves.PERM_STAFF)) {
            player.sendMessage("§cYou do not have permission to use this command"); return true
        }

        // Toggle chat if no args
        if (args.isEmpty()) { toggleGroupChat(player, groupChat); return true }

        // Send message in group chat
        val rawMessage = args.joinToString(" ")
        sendMessageInGroupChat(player, groupChat, rawMessage)

        return true
    }

    // Chat listener for toggled chats
    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player
        val toggledChat = TOGGLED_GROUP_CHATS[player] ?: return

        // Cancel normal chat and send in group chat
        event.isCancelled = true
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        sendMessageInGroupChat(player, toggledChat, message)
    }

    // Function to send message in group chat
    private fun sendMessageInGroupChat(player: Player, groupChat: GroupChats, rawMessage: String) {
        // Format message
        val message = groupChat.prefix + groupChat.formatting
            .replace("%player%", player.name)
            .replace("%message%", rawMessage)

        // Send the message to players with view perm or staff perm
        Utils.messagePlayerWithPerm(message, groupChat.permissionView, Saves.PERM_STAFF)
    }

    // Function to toggle group chat
    private fun toggleGroupChat(player: Player, groupChat: GroupChats) {
        val currentToggle = TOGGLED_GROUP_CHATS[player]
        // Remove if same chat
        if (currentToggle == groupChat) {
            TOGGLED_GROUP_CHATS.remove(player)
            player.sendMessage("${groupChat.prefix}§7Toggled §cOFF §7messages for ${groupChat.name}")
        }
        // Add or replace with new chat
        else {
            TOGGLED_GROUP_CHATS[player] = groupChat
            player.sendMessage("${groupChat.prefix}§7Toggled §aON §7messages for ${groupChat.name}")
        }
    }

    // Function to register group chat commands
    private fun registerCommands() {
        GroupChats.entries.forEach { groupChat ->
            groupChat.command.forEach { command ->
                Utils.registerCommand(command, this)
            }
        }
    }
}
