package com.jjkay03.nationsevent.group_chat

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class GroupChatsCommands(private val groupChats: GroupChats) : CommandExecutor {

    companion object {
        // Register group chat commands
        fun registerGroupChatCommands(plugin: JavaPlugin) {
            GroupChats.entries.forEach { groupChat ->
                groupChat.command.forEach { command ->
                    Utils.registerCommand(plugin, command, GroupChatsCommands(groupChat))
                }
            }
        }
    }

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command"); return true }
        val player = sender

        // End if player doesn't have send pem or staff perm
        if (!player.hasPermission(groupChats.permissionSend) || !player.hasPermission(Saves.PERM_STAFF)) {
            player.sendMessage("§cYou do not have permission to use this command"); return true
        }

        // End if no arguments (message) is provided
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$label <message>"); return true }

        // Format message
        val rawMessage = args.joinToString(" ")
        val message = groupChats.formatting
            .replace("%player%", player.name)
            .replace("%message%", rawMessage)

        // Send the message to players with view pem or staff pem
        Utils.messagePlayerWithPerm(message, groupChats.permissionView, Saves.PERM_STAFF)

        return true
    }
}