package com.jjkay03.nationsevent.group_chat_players

import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class AdminGroupChatCommand : CommandExecutor, TabCompleter {

    companion object {
        private val OPTIONS = listOf("add", "chat", "create", "delete", "getid", "kick", "list", "listall", "setowner", "togglespy")
    }

    /*   /agc add <groupChatID> [player]
     *   /agc chat <groupChatID> <message>
     *   /agc create [owner] [players...]
     *   /agc delete <groupChatID>
     *   /agc getid <player>
     *   /agc kick <player>
     *   /agc list <groupChatID>
     *   /agc listall
     *   /agc setowner <groupChatID> <newOwner>
     *   /agc togglespy [groupChatID]
     */

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!OPTIONS.contains(args[0])) { sender.sendMessage("§cUsage: /$label <add/chat/create/delete/getid/kick/list/listall/setowner/togglespy>"); return true }

        // Deal with arguments
        when (args[0].lowercase()) {

            // ADD
            "add" -> {

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                // Gets the player that is to join the GC
                val player = Bukkit.getOfflinePlayerIfCached(args[2])
                if (player == null) { sender.sendMessage("§c\"${args[2]}\" is not a cached player!"); return true }

                // Forces 'player' to join the GC
                PlayerGroupChatUtils.joinGroupChat(player, PlayerGroupChatUtils.getOwner(target)!!, true)
                if (player.isOnline) { player.player!!.sendMessage("§aYou were forcefully put into GC${target} by Staff!") }

                sender.sendMessage("§aForced ${player.name} to join GC${target}")
            }

            // CHAT
            "chat" -> {
                if (args.size < 3) { sender.sendMessage("§cUsage: /$label chat <groupChatID> <message>"); return true }

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                // Get message to send in GC
                val message = StringBuilder()
                for (i in 2..<args.size) { message.append(args[i]); if (i != args.size - 1) message.append(" ") }

                PlayerGroupChatUtils.sendInGroupChat(target, "${sender.name}: $message", true)
            }

            // CREATE
            "create" -> {

                // No [owner] argument
                if (args.size == 1) {
                    if (sender !is Player) { sender.sendMessage("§cUsage: /$label create <owner> [players...]") }
                    else { sender.performCommand("groupchat create") }
                    return true
                }

                // Get 'owner' for the new group chat
                val owner = Bukkit.getOfflinePlayerIfCached(args[1])
                if (owner == null) { sender.sendMessage("§c\"${args[1]}\" is not a cached player!"); return true }

                // If players were given, add those players to the GC
                val targets = mutableListOf<OfflinePlayer>()
                if (args.size > 2) {
                    for (i in 2..<args.size) {
                        val player = Bukkit.getOfflinePlayerIfCached(args[i])
                        if (player == null) { sender.sendMessage("§c\"${args[i]}\" is not a cached player!"); return true }
                        targets.add(player)
                    }
                }

                // Create the group chat and put the players in
                val result = PlayerGroupChatUtils.createGroupChat(owner, true)
                targets.forEach { PlayerGroupChatUtils.joinGroupChat(it, owner, true) }

                sender.sendMessage("§aCreated group chat GC${result.second}")
            }

            // DELETE
            "delete" -> {

                if (args.size == 1) { sender.sendMessage("§cUsage: /$label delete <groupChatID>"); return true }

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                // Delete the group chat
                val result = PlayerGroupChatUtils.deleteGroupChat(PlayerGroupChatUtils.getOwner(target)!!, true)
                sender.sendMessage("§aDeleted group chat GC${result.second}")
            }

            // GET
            "getid" -> {

                if (args.size == 1) { sender.sendMessage("§cUsage: /$label getid <player>"); return true }

                // Gets the player if the player has logged on the server before (a.k.a. is cached)
                val player = Bukkit.getOfflinePlayerIfCached(args[1])
                if (player == null) { sender.sendMessage("§c\"${args[1]}\" is not a cached player!"); return true }

                if (!PlayerGroupChatUtils.hasGroupChat(player)) { sender.sendMessage("§cThis player is not in a group chat!"); return true }

                // Sends a hoverable message with the specified player's GC
                sender.sendMessage(Component
                    .text("§a${player.name} is in §2§nGC${PlayerGroupChatUtils.getGroupChatID(player)}§a §8(hover)")
                    .hoverEvent(PlayerGroupChatUtils.createGroupChatMessageHover(PlayerGroupChatUtils.getGroupChatID(player)))
                )
            }

            // KICK
            "kick" -> {

                if (args.size == 1) { sender.sendMessage("§cUsage: /$label kick <player>"); return true }

                // Gets the player that is to get kicked
                val player = Bukkit.getOfflinePlayerIfCached(args[1])
                if (player == null) { sender.sendMessage("§c\"${args[1]}\" is not a cached player!"); return true }

                // Forces 'player' to be kicked from whatever GC they're in
                val result = PlayerGroupChatUtils.leaveGroupChat(player, adminForce = true)

                // Command feedback
                if (result.first) {
                    sender.sendMessage("§aForced ${player.name} to leave GC${result.second}")
                    if (player.isOnline) { player.player!!.sendMessage("§cYou were kicked from GC${result.second} by Staff!") }
                }
                else { sender.sendMessage("§cThis player is not in a group chat!") }
            }

            // LIST
            "list" -> {

                if (args.size == 1) { sender.sendMessage("§cUsage: /$label list <groupChatID>"); return true }

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                // Sends a hoverable message with the specified player's GC
                sender.sendMessage(Component
                    .text("§aList of §2§nGC$target§a members §8(hover)")
                    .hoverEvent(PlayerGroupChatUtils.createGroupChatMessageHover(target))
                )
            }

            // LISTALL
            "listall" -> {

                // Gets a message with hoverable text for each existing group chat
                val message = Component.text().append(Component.text("§aList of all group chats §8(hover)§a: "))
                GROUP_CHATS.toSortedMap().forEach {
                    message.append(Component.text("§2§nGC${it.key}").hoverEvent(PlayerGroupChatUtils.createGroupChatMessageHover(it.key)))
                    if (it.key != GROUP_CHATS.size - 1) { message.append(Component.text("§a, ")) }
                }

                // Sends the list created above with intermediate lines
                sender.sendMessage(" ")
                sender.sendMessage(message)
                sender.sendMessage(" ")
            }

            // SETOWNER
            "setowner" -> {

                if (args.size < 3) { sender.sendMessage("§cUsage: /$label setowner <groupChatID> <player>"); return true }

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                // Gets the player that is to become the new owner
                val player = Bukkit.getOfflinePlayerIfCached(args[2])
                if (player == null) { sender.sendMessage("§c\"${args[2]}\" is not a cached player!"); return true }

                val owner = PlayerGroupChatUtils.getOwner(target)!!

                // Sets the new owner of 'owner's' group chat as 'player'
                PlayerGroupChatUtils.setOwner(owner, player, true)
                sender.sendMessage("§aSet ${player.name} as the new owner of GC${target}")
                owner.player!!.sendMessage("§cYou are no longer your group chat's owner!")
            }

            // TOGGLESPY
            "togglespy" -> {

                if (sender !is Player) { sender.sendMessage("§cThis command is only usable by players!"); return true }

                // Get group chat ID
                val target = args[1].toIntOrNull()
                if (target == null || !PlayerGroupChatUtils.isGroupChat(target)) { sender.sendMessage("§cGroup chat ID does not exist!"); return true }

                PlayerGroupChatUtils.setSpy(sender, target)
                sender.sendMessage("§aNow spying on GC$target")
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> OPTIONS.filter { it.startsWith(args[0], true) }
            2 -> return when (args[0]) {
                "add", "chat", "delete", "list", "setowner" -> listOf("<groupChatID>")
                "create", "getid", "kick" -> Utils.matchOnlinePlayers(args[1])
                "togglespy" -> listOf("[groupChatID]")
                else -> listOf()
            }
            3 -> return when (args[0]) {
                "add", "create", "setowner" -> Utils.matchOnlinePlayers(args[2])
                "chat" -> listOf("<message>")
                else -> listOf()
            }
            else -> return when (args[0]) {
                "chat" -> listOf("<message>")
                "create" -> Utils.matchOnlinePlayers(args[args.size-1])
                else -> listOf()
            }
        }
    }
}