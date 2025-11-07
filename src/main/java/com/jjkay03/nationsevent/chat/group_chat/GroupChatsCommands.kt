package com.jjkay03.nationsevent.chat.group_chat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.UUID

class GroupChatsCommands : CommandExecutor, Listener {

    companion object {
        // Function to register group chat commands
        fun registerCommands() {
            NationsEvent.INSTANCE.logger.info("- Loading Group Chats (${GroupChats.entries.size})")
            val commandInstance = GroupChatsCommands()
            GroupChats.entries.forEach { groupChat ->
                groupChat.command.forEach { command ->
                    Utils.registerCommand(command, commandInstance)
                }
            }
        }

        private val toggledStaff = mutableListOf<UUID>()
    }

    // Register event
    init { NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE) }

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

        // End if no arguments (message) is provided
        if (args.isEmpty()) {
            // Toggle staff chat
            if (groupChat == GroupChats.STAFF_CHAT) {
                if (toggledStaff.contains(player.uniqueId)) {
                    toggledStaff.remove(player.uniqueId)
                    sender.sendMessage("§cToggled staff chat off!")
                }
                else {
                    toggledStaff.add(player.uniqueId)
                    sender.sendMessage("§aToggled staff chat on!")
                }
            }
            else { sender.sendMessage("§cUsage: /${cmd.name} <message>") }
            return true
        }

        // Format message
        val rawMessage = args.joinToString(" ")
        val message = groupChat.formatting
            .replace("%player%", player.name)
            .replace("%message%", rawMessage)

        // Send the message to players with view perm or staff perm
        Utils.messagePlayerWithPerm(message, groupChat.permissionView, Saves.PERM_STAFF)

        return true
    }

    // Staff chat overrider
    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        if (!toggledStaff.contains(e.player.uniqueId)) return
        e.isCancelled = true

        val groupChat = GroupChats.STAFF_CHAT
        val message = groupChat.formatting
            .replace("%player%", e.player.name)
            .replace("%message%", e.message)

        Utils.messagePlayerWithPerm(message, groupChat.permissionView, Saves.PERM_STAFF)
    }
}
