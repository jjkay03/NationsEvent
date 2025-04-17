package com.jjkay03.nationsevent.group_chat_players

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class AdminGroupChatCommand : CommandExecutor, TabCompleter {

    companion object {
        private val OPTIONS = listOf("chat", "create", "delete", "get", "join", "kick", "list", "listall", "setowner", "togglespy")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!OPTIONS.contains(args[0])) { sender.sendMessage("§cUsage: /$label <chat/create/delete/get/join/kick/list/listall/setowner/togglespy>"); return true }

        /*
         *   /agc chat <groupChatID> <message>
         *   /agc create [owner] [players...]
         *   /agc delete <groupChatID>
         *   /agc get <player>
         *   /agc join <groupChatID> [player]
         *   /agc kick <player>
         *   /agc list <groupChatID>
         *   /agc listall
         *   /agc setowner <groupChatID> <newOwner>
         *   /agc togglespy <groupChatID>
         */

        // Deal with arguments
        when (args[0].lowercase()) {

            // CHAT
            "chat" -> {
                if (args.size < 3) { sender.sendMessage("§cUsage: /$label chat <groupChatID> <message>"); return true }

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                // Get message to send in GC
                val message = StringBuilder()
                for (i in 2..<args.size) { message.append(args[i]); if (i != args.size - 1) message.append(" ") }

                PlayerGroupChatUtils.sendInGroupChat(target, "§c[ADMIN]§r ${sender.name}: $message")
            }

            // CREATE
            "create" -> {
                if (args.size == 1) {
                    if (sender !is Player) { sender.sendMessage("§cUsage: /$label create <owner> [players...]") }
                    else { sender.performCommand("groupchat create") }
                    return true
                }

                val owner = Bukkit.getOfflinePlayerIfCached(args[1])
                if (owner == null) { sender.sendMessage("§c\"${args[1]}\" is not a cached player!"); return true }

                val targets = mutableListOf<OfflinePlayer>()
                if (args.size > 2) {
                    for (i in 2..<args.size) {
                        val player = Bukkit.getOfflinePlayerIfCached(args[i])
                        if (player == null) { sender.sendMessage("§c\"${args[i]}\" is not a cached player!"); return true }
                        targets.add(player)
                    }
                }

                PlayerGroupChatUtils.createGroupChat(owner, true)
                targets.forEach { PlayerGroupChatUtils.joinGroupChat(it, owner) }
            }


        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        TODO("Not yet implemented")
    }
}