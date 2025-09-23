package com.jjkay03.nationsevent.chat.group_chat_players.commands

import com.jjkay03.nationsevent.Utils
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChat
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChatUtils
import com.jjkay03.nationsevent.chat.group_chat_players.PlayerGroupChatManager
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import kotlin.collections.filter
import kotlin.text.lowercase
import kotlin.text.startsWith

class AdminGroupChatCommand : CommandExecutor, TabCompleter {

    // SUBCOMMANDS - names and perms
    enum class SubCommand(val cmd: String) {
        COORDS("coords"),
        CHAT("chat"),
        CREATE("create"),
        DELETE("delete"),
        DELETEALL("deleteall"),
        ADD("add"),
        REMOVE("remove"),
        JOIN("join"),
        LIST("list"),
        SETOWNER("setowner"),
        SPY("spy");
        val perm: Permission get() = Permission("nationsevent.command.admingroupchat.$cmd")
    }

    val commandUsage = "§cUsage: " + (SubCommand.entries.joinToString("/") { it.cmd })

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Checks
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eOnly players can use this command!"); return true;}
        if (args.isEmpty()) {sender.sendMessage(commandUsage); return true;}

        val player = sender as Player

        // Deal with arguments
        when (args[0].lowercase()) {

            // COORDS
            SubCommand.COORDS.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.COORDS)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label ${SubCommand.COORDS.cmd} <ID>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Send the message
                PlayerGroupChatUtils.chatCoords(groupChat, player, true)
            }

            // CHAT
            SubCommand.CHAT.cmd, "c" -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.CHAT)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.CHAT.cmd} <ID> <message>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Send the message
                val message = args.drop(2).joinToString(" ")
                PlayerGroupChatUtils.chat(groupChat, player, message, true)
            }

            // CREATE
            SubCommand.CREATE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.CREATE)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.CREATE.cmd} <name> <owner> <players...>"); return true }

                // Check - validate group chat name
                if (!PlayerGroupChatUtils.validateGCName(args[1])) {
                    player.sendMessage("§c${PlayerGroupChatManager.PREFIX}Invalid group chat name, use only letters and max ${Config.PGC_NAME_CHARACTER_LIMIT} characters!")
                    return true
                }

                // Get target player (owner) - end if invalid player
                val targetPlayerOwner = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player ${args[2]}!").let { true }

                // Get target players (members) - end if invalid player
                val targetPlayersMembers = mutableListOf<Player>()
                args.drop(3).forEach {
                    Bukkit.getPlayer(it)?.let { player -> targetPlayersMembers.add(player) } ?:
                    return player.sendMessage("§cInvalid player $it!").let { true }
                }

                // Create group chat
                val groupChat = PlayerGroupChatUtils.createGC(targetPlayerOwner, targetPlayersMembers, args[1], true)
                if (groupChat == null) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}Failed to create group chat, ${targetPlayerOwner.name} has reached the group chat limit!"); return true }

                // Notify players
                val targetPlayersNames = groupChat.playerList.mapNotNull { it.name }.joinToString(", ")
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §aCREATED §7group chat %gc% §7with $targetPlayersNames", "§2", groupChat))
                val targetPlayersMembersMsg = PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You were §aADDED §7to group chat %gc% §7by staff", "§7", groupChat)
                Utils.messagePlayerList(groupChat.playerList, targetPlayersMembersMsg)

                // Alert group chat members
                PlayerGroupChatUtils.chat(groupChat, null, "Staff added $targetPlayersNames to group chat")
            }

            // DELETE
            SubCommand.DELETE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.DELETE)) return true

                // Check - validate arguments and confirmation
                if (args.size < 3 || args[2] != "CONFIRM") { player.sendMessage("§cUsage: /$label ${SubCommand.DELETE.cmd} <ID> CONFIRM"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Notify player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cDELETED §7group chat %gc%", "§c", groupChat))

                // Alert group chat members and delete
                PlayerGroupChatUtils.chat(groupChat, null, "Staff deleted group chat")
                PlayerGroupChatUtils.deleteGC(groupChat)
            }

            // DELETEALL
            SubCommand.DELETEALL.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.DELETEALL)) return true

                // Check - validate arguments and confirmation
                if (args.size < 2 || args[1] != "CONFIRM") { player.sendMessage("§cUsage: /$label ${SubCommand.DELETEALL.cmd} CONFIRM"); return true }

                // Go through all groups
                for ( group in PlayerGroupChatManager.GROUP_CHATS.toList() ) {
                    // Notify player
                    player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cDELETED §7group chat %gc%", "§c", group))

                    // Alert group chat members and delete
                    PlayerGroupChatUtils.chat(group, null, "Staff deleted group chat")
                    PlayerGroupChatUtils.deleteGC(group)
                }
            }

            // ADD
            SubCommand.ADD.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.ADD)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.ADD.cmd} <ID> <players...>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Get target players (members) - end if invalid player
                val targetPlayers = mutableListOf<Player>()
                args.drop(2).forEach {
                    // End if player invalid
                    val target = Bukkit.getPlayer(it) ?: return player.sendMessage("§cInvalid player $it!").let { true }

                    // Skip if player is already in group
                    if (groupChat.playerList.contains(target)) { return@forEach }
                    targetPlayers.add(target)
                }

                // Add players to group
                PlayerGroupChatUtils.addPlayerToGC(groupChat, targetPlayers, true)

                // Notify players
                val targetNames = if (targetPlayers.size == 1) targetPlayers.first().name else targetPlayers.joinToString(", ") { it.name }
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §aADDED §7$targetNames to %gc%", "§7", groupChat))
                val targetPlayersMsg = PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You were §aADDED §7to group chat %gc% §7by staff", "§7", groupChat)
                Utils.messagePlayerList(targetPlayers, targetPlayersMsg)

                // Alert group chat members
                if (targetNames != "") PlayerGroupChatUtils.chat(groupChat, null, "Staff added $targetNames to group chat")

            }

            // REMOVE
            SubCommand.REMOVE.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.REMOVE)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.REMOVE.cmd} <ID> <players...>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Get target players (members) - end if invalid player
                val targetPlayers = mutableListOf<Player>()
                args.drop(2).forEach {
                    // End if player invalid
                    val target = Bukkit.getPlayer(it) ?: return player.sendMessage("§cInvalid player $it!").let { true }

                    // Skip if player is not in group
                    if (!groupChat.playerList.contains(target)) { player.sendMessage("§c$it is not in the group chat!"); return@forEach }
                    targetPlayers.add(target)
                }

                // Add players to group
                PlayerGroupChatUtils.removePlayerFromGC(groupChat, targetPlayers, true)

                // Notify players
                val targetNames = if (targetPlayers.size == 1) targetPlayers.first().name else targetPlayers.joinToString(", ") { it.name }
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cREMOVED §7$targetNames from %gc%", "§7", groupChat))
                val targetPlayersMsg = PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You were §cREMOVED §7from group chat %gc% §7by staff", "§7", groupChat)
                Utils.messagePlayerList(targetPlayers, targetPlayersMsg)

                // Alert group chat members
                PlayerGroupChatUtils.chat(groupChat, null, "Staff removed $targetNames from group chat")
            }

            // JOIN
            SubCommand.JOIN.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.JOIN)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label ${SubCommand.JOIN.cmd} <ID>"); return true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check if player is already a member of group
                if (groupChat.playerList.contains(player)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}You are already a member of ${groupChat.prefix}!") }

                // Add player to group
                PlayerGroupChatUtils.addPlayerToGC(groupChat, listOf(player))

                // Alert player
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §aJOINED §7group chat %gc%", "§7", groupChat))

                // Send join message in group chat
                PlayerGroupChatUtils.chat(groupChat, null, "${player.name} joined group")
            }

            // LIST
            SubCommand.LIST.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.LIST)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label ${SubCommand.LIST.cmd} <player/@a>"); return true }

                // Deal with argument
                when (args[1].lowercase()) {
                    // List all groups
                    "@a", "all" -> {
                        // List all server groups
                        player.sendMessage("")
                        player.sendMessage("§e${PlayerGroupChatManager.PREFIX}All server group chats:")
                        player.sendMessage ("§7(Hover GC for more info")
                        for (group in PlayerGroupChatManager.GROUP_CHATS) {
                            val line = if (group.owner == player) "  §f• %gc% §6👑" else "  §f• %gc%"
                            player.sendMessage(
                                PlayerGroupChatUtils.formatHoverableMessage(line, "§f", group,
                                underLined = false,
                                isWholeMessageHoverable = true
                            ))
                        }
                        player.sendMessage("")
                    }

                    // List all groups a player is in
                    else -> {
                        // Get target player (owner) - end if invalid player
                        val targetPlayer = Bukkit.getPlayer(args[1]) ?: return player.sendMessage("§cInvalid player ${args[1]}!").let { true }

                        // List groups player is in
                        player.sendMessage("")
                        player.sendMessage("§e${PlayerGroupChatManager.PREFIX}Group chats ${targetPlayer.name} is in:")
                        player.sendMessage ("§7(Hover GC for more info")
                        for (group in PlayerGroupChatUtils.getPlayerGCs(targetPlayer)) {
                            val line = if (group.owner == targetPlayer) "  §f• %gc% §6👑" else "  §f• %gc%"
                            player.sendMessage(
                                PlayerGroupChatUtils.formatHoverableMessage(line, "§f", group,
                                underLined = false,
                                isWholeMessageHoverable = true
                            ))
                        }
                        player.sendMessage("")
                    }
                }
            }

            // SETOWNER
            SubCommand.SETOWNER.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.SETOWNER)) return true

                // Check - validate arguments
                if (args.size < 3) { player.sendMessage("§cUsage: /$label ${SubCommand.SETOWNER.cmd} <ID> <player>"); return true }

                // Get target player
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return player.sendMessage("§cInvalid player!").let { true }

                // Check - get and validate group chat
                val groupChat = getAndValidateGC(args[1], player) ?: return true

                // Check - if player is member of group chat or already owner
                if (!groupChat.playerList.contains(targetPlayer)) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}${targetPlayer.name} is not a member of ${groupChat.prefix}!"); return true }
                if (targetPlayer == groupChat.owner) { player.sendMessage("§c${PlayerGroupChatManager.PREFIX}${targetPlayer.name} is already the owner of ${groupChat.prefix}!"); return true }

                // Set new owner
                PlayerGroupChatUtils.setGCOwner(groupChat, targetPlayer)

                // Notify players
                player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §bTRANSFERRED §7group chat %gc% §7to ${targetPlayer.name}", "§7", groupChat))
                targetPlayer.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}Group chat %gc% §7was §bTRANSFERRED §7to you by staff", "§7", groupChat))
            }

            // SPY
            SubCommand.SPY.cmd -> {
                // Check - subcommand permission
                if (!checkSubCommandPerm(player, SubCommand.SPY)) return true

                // Check - validate arguments
                if (args.size < 2) { player.sendMessage("§cUsage: /$label ${SubCommand.SETOWNER.cmd} <ID/@a/CLEARALL>"); return true }

                // Deal with argument
                when (args[1].lowercase()) {
                    // Clear all spied on groups
                    "clearall", "clear" -> {
                        PlayerGroupChatUtils.spyRemoveAll(player)
                        player.sendMessage("§7${PlayerGroupChatManager.PREFIX}You CLEARED §dSPYING §7on all group chats")
                    }

                    // Spy on all
                    "@a", "all" -> {
                        // Check if player is global spy
                        if (!PlayerGroupChatManager.GLOBAL_SPIES.contains(player)) {
                            PlayerGroupChatManager.GLOBAL_SPIES.add(player)
                            player.sendMessage("§7${PlayerGroupChatManager.PREFIX}You §aENABLED §dGLOBAL SPYING §7on all group chats")
                        } else {
                            PlayerGroupChatManager.GLOBAL_SPIES.remove(player)
                            player.sendMessage("§7${PlayerGroupChatManager.PREFIX}You §cDISABLED §dGLOBAL SPYING §7on all group chats")
                        }

                    }

                    // Spy on specific group
                    else -> {
                        // Check - get and validate group chat
                        val groupChat = getAndValidateGC(args[1], player) ?: return true

                        // Add/remove spy to group + notify player
                        if (PlayerGroupChatUtils.spyAdd(groupChat, player)) {
                            player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §aENABLED §dSPYING §7on group chat %gc%", "§7", groupChat))
                        } else {
                            PlayerGroupChatUtils.spyRemove(groupChat, player)
                            player.sendMessage(PlayerGroupChatUtils.formatHoverableMessage("§7${PlayerGroupChatManager.PREFIX}You §cDISABLED §dSPYING §7on group chat %gc%", "§7", groupChat))
                        }
                    }
                }
            }

            // NO ARGS - Display command usage
            else -> player.sendMessage(commandUsage)

        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (sender !is Player) return emptyList()
        val sub = args.getOrNull(0)?.lowercase() ?: return emptyList()
        val current = args.last().lowercase()

        return when (args.size) {
            // First argument - command list
            1 -> SubCommand.entries.filter { sender.hasPermission(it.perm) }.map { it.cmd }.filter { it.startsWith(sub, true) }

            // 2nd argument - context-specific completions
            2 -> when (sub) {
                // Show all group chats
                SubCommand.COORDS.cmd, SubCommand.CHAT.cmd, "c", SubCommand.DELETE.cmd, SubCommand.ADD.cmd, SubCommand.REMOVE.cmd, SubCommand.JOIN.cmd, SubCommand.SETOWNER.cmd ->
                    PlayerGroupChatUtils.tabCompletePlayerGCsList(PlayerGroupChatManager.GROUP_CHATS).filter { it.lowercase().startsWith(current) }

                // Show all group chats + @a + CLEAR
                SubCommand.SPY.cmd -> (listOf("@a", "CLEARALL") + PlayerGroupChatUtils.tabCompletePlayerGCsList(
                    PlayerGroupChatManager.GROUP_CHATS
                )).filter { it.lowercase().startsWith(current) }

                // Show all players + @a
                SubCommand.LIST.cmd -> (listOf("@a") + Bukkit.getOnlinePlayers().map { it.name }).filter { it.lowercase().startsWith(current) }

                // Show argument
                SubCommand.CREATE.cmd -> listOf("<name>").filter { it.startsWith(current, true) }

                else -> emptyList()
            }

            // 3rd+ arguments - context-specific completions
            in 3..Int.MAX_VALUE -> when (sub) {

                // Show group members (only when 3 args)
                SubCommand.SETOWNER.cmd -> if (args.size == 3) {
                    val gc = PlayerGroupChatUtils.tabCompleteInputGCGet(args[1]) ?: return emptyList()
                    gc.playerList.mapNotNull { it.name }.filter { it.lowercase().startsWith(current) }
                } else emptyList()

                // Show group members
                SubCommand.REMOVE.cmd -> {
                    val gc = PlayerGroupChatUtils.tabCompleteInputGCGet(args[1]) ?: return emptyList()
                    gc.playerList.mapNotNull { it.name }.filter { it.lowercase().startsWith(current) }
                }

                // Show all players
                SubCommand.ADD.cmd, SubCommand.CREATE.cmd -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(current) }

                else -> emptyList()
            }

            else -> emptyList()
        }
    }

    // Helper function to check if sender has permission to use subcommand
    private fun checkSubCommandPerm(sender: CommandSender, subCommand: SubCommand): Boolean {
        if (sender.hasPermission(subCommand.perm)) return true
        else {
            sender.sendMessage("§cYou do not have permission to use '${subCommand.cmd}' subcommand!")
            return false
        }
    }

    // Helper function use to get validate a group input
    private fun getAndValidateGC(groupChatArgument: String, sender: CommandSender): PlayerGroupChat? {
        val groupChat = PlayerGroupChatUtils.tabCompleteInputGCGet(groupChatArgument)
        if (groupChat == null) { sender.sendMessage("§c${PlayerGroupChatManager.PREFIX}Invalid group chat ID!"); return null }
        else return groupChat
    }
}