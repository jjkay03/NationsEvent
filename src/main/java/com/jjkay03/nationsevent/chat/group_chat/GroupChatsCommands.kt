package com.jjkay03.nationsevent.chat.group_chat

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GroupChatsCommands : CommandExecutor {

    companion object {
        // Function to register group chat commands
        fun registerGroupChatsCommands() {
            NationsEvent.INSTANCE.logger.info("- Loading Group Chats (${GroupChats.entries.size})")
            val commandInstance = GroupChatsCommands()
            GroupChats.entries.forEach { groupChat ->
                groupChat.command.forEach { command ->
                    Utils.registerCommand(command, commandInstance)
                }
            }
        }
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

        // End if no arguments (message) is provided
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /${cmd.name} <message>"); return true }

        // Format message
        val rawMessage = args.joinToString(" ")
        val message = groupChat.formatting
            .replace("%player%", player.name)
            .replace("%message%", rawMessage)

        // Send the message to players with view perm or staff perm
        Utils.messagePlayerWithPerm(message, groupChat.permissionView, Saves.PERM_STAFF)

        return true
    }
}
