package com.jjkay03.nationsevent.group_chat_players

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PlayerGroupChatsCommand : CommandExecutor, TabCompleter {

    companion object {
        private val OPTIONS = listOf("chat", "create", "delete", "invite", "join", "kick", "leave", "list", "setowner")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()) {sender.sendMessage("§cUsage: /$label <chat|create|delete|invite|join|leave|setowner>"); return true;}
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eUse /admingroupchat instead. This command is for players only"); return true;}

        val player = sender as Player

        when (args[0].lowercase()) {

            "create" -> {
                val result = PlayerGroupChatsManager.createGroupChat(player)

                if (result.first) { player.sendMessage("§aCreated group chat \"GC${result.second}\"") }
                else { player.sendMessage("§cYou are already in a group chat! Leave it or delete it to make a new one") }
            }

            "delete" -> {
                val result = PlayerGroupChatsManager.deleteGroupChat(player)

                if (result.first) { player.sendMessage("§aDeleted group chat \"GC${result.second}\"") }
                else { player.sendMessage("§cYou are not the owner of this group chat!") }
            }

            "invite" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label invite <player>"); return true }

                val invited = Bukkit.getPlayerExact(args[1])
                if (invited == null) { player.sendMessage("§cThis player is offline!"); return true }

                PlayerGroupChatsManager.inviteToGroupChat(player, invited)
            }

            "join" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label join <player>"); return true }

                val inviteSender = Bukkit.getPlayerExact(args[1])
                if (inviteSender == null) { player.sendMessage("§cThis player is offline!"); return true }

                val result = PlayerGroupChatsManager.joinGroupChat(player, inviteSender)

                if (result.first) { player.sendMessage("§aYou have joined ${inviteSender.name}'s group chat ") }
                else { player.sendMessage("§cYou have not received an invite to ${inviteSender.name}'s group chat ${PlayerGroupChatsManager.getGroupChatName(result.second)}!") }
            }

            "kick" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label kick <player>"); return true }

                val toKick = Bukkit.getPlayerExact(args[1])
                if (toKick == null) { player.sendMessage("§cThis player is offline!"); return true }

                val result = PlayerGroupChatsManager.leaveGroupChat(toKick)

                if (result.first) { toKick.sendMessage("§cYou have been kicked from group chat ${PlayerGroupChatsManager.getGroupChatName(result.second)}") }
                else {
                    if (result.second != -1) { toKick.sendMessage("§cYou are not the owner of this group chat!") }
                    else { toKick.sendMessage("§cThis player is not in your group chat!") }
                }
            }

            "leave" -> {
                val result = PlayerGroupChatsManager.leaveGroupChat(player)

                if (result.first) { player.sendMessage("§aYou have left group chat ${PlayerGroupChatsManager.getGroupChatName(result.second)}") }
                else { player.sendMessage("§cYou are not in a group chat!") }
            }

            "list" -> {
                if (!PlayerGroupChatsManager.isInAGroupChat(player)) { player.sendMessage("§cYou are not in a group chat!"); return true }

                player.sendMessage("§a${PlayerGroupChatsManager.getGroupChatName(PlayerGroupChatsManager.getGroupChatID(player))} player list:\n")
                PlayerGroupChatsManager.getPlayerList(player).forEach { p -> player.sendMessage("§a - ${p.name}") }
            }

            "setowner" -> {
                if (args.size < 2) { player.sendMessage("§cUsage: /$label setowner <player>"); return true }

                val newOwner = Bukkit.getPlayerExact(args[1])
                if (newOwner == null) { player.sendMessage("§cThis player is offline!"); return true }

                val result = PlayerGroupChatsManager.setOwner(player, newOwner)

                if (result.first) {
                    if (result.second != -1) { player.sendMessage("§aSet ${newOwner.name} as the new group chat owner") }
                    else { player.sendMessage("§cThis player is not in your group chat!") }
                }
                else {
                    if (result.second != -1) { player.sendMessage("§cYou are not the owner of this group chat!") }
                    else { player.sendMessage("§cYou are not in a group chat!") }
                }
            }

            else -> {
                val startIndex = if (args[0] == "chat") 1 else 0

                val message = StringBuilder()
                for (i in startIndex..args.size) {message.append(" ").append(args[i])}

                PlayerGroupChatsManager.sendInGroupChat(player, message.toString())
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        return when (args.size) {
            1 -> OPTIONS.filter { op -> op.contains(args[0], true) }.toMutableList().ifEmpty { mutableListOf("<message>") }
            2 -> if (args[0] == "invite" || args[0] == "join" || args[0] == "kick" || args[0] == "setowner") mutableListOf("<player>") else mutableListOf()
            else -> mutableListOf()
        }
    }
}