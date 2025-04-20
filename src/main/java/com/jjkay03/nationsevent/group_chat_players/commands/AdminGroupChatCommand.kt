package com.jjkay03.nationsevent.group_chat_players.commands

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatUtils
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

    /*

    ✅ Tab complete

    ❌ /agc coords <gc>
    ❌ /agc chat <gc> message
    ❌ /agc create <name> <owner> <players...>
    ❌ /agc delete <ID> CONFIRM
    ❌ /agc add <ID> <players...>
    ❌ /agc remove <ID> <players...>
    ❌ /agc list <player / @a>
    ❌ /agc setowner <ID> <player>
    ❌ /agc spy <ID / @a>

     */

    // SUBCOMMANDS - names and perms
    enum class SubCommand(val cmd: String) {
        COORDS("coords"),
        CHAT("chat"),
        CREATE("create"),
        DELETE("delete"),
        ADD("add"),
        REMOVE("remove"),
        LIST("list"),
        SETOWNER("setowner"),
        SPY("spy");
        val perm: Permission get() = Permission("nationsevent.command.admingroupchat.$cmd")
    }

    val commandUsage = "§cUsage:" + (SubCommand.entries.joinToString("/") { it.cmd })

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Checks
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eOnly players can use this command!"); return true;}
        if (args.isEmpty()) {sender.sendMessage(commandUsage); return true;}

        val player = sender as Player

        // Deal with arguments
        when (args[0].lowercase()) {

            // COORDS
            SubCommand.COORDS.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // CHAT
            SubCommand.CHAT.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // CREATE
            SubCommand.CREATE.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // DELETE
            SubCommand.DELETE.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // ADD
            SubCommand.ADD.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // REMOVE
            SubCommand.REMOVE.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // LIST
            SubCommand.LIST.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // SETOWNER
            SubCommand.SETOWNER.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
            }

            // SPY
            SubCommand.SPY.name -> {
                player.sendMessage("§cNOT IMPLEMENTED YET!") // TODO
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
                "coords", "chat", "delete", "add", "remove", "setowner" ->
                    PlayerGroupChatUtils.tabCompletePlayerGCsList(GROUP_CHATS).filter { it.lowercase().startsWith(current) }

                // Show all group chats + @a
                "spy" -> (listOf("@a") + PlayerGroupChatUtils.tabCompletePlayerGCsList(GROUP_CHATS)).filter { it.lowercase().startsWith(current) }

                // Show all players + @a
                "list" -> (listOf("@a") + Bukkit.getOnlinePlayers().map { it.name }).filter { it.lowercase().startsWith(current) }

                // Show argument
                "create" -> listOf("<name>").filter { it.startsWith(current, true) }

                else -> emptyList()
            }

            // 3rd+ arguments - context-specific completions
            in 3..Int.MAX_VALUE -> when (sub) {

                // Show group members (only when 3 args)
                "setowner" -> if (args.size == 3) {
                    val gc = PlayerGroupChatUtils.tabCompleteInputGCGet(args[1]) ?: return emptyList()
                    gc.playerList.mapNotNull { it.name }.filter { it.lowercase().startsWith(current) }
                } else emptyList()

                // Show group members
                "remove" -> {
                    val gc = PlayerGroupChatUtils.tabCompleteInputGCGet(args[1]) ?: return emptyList()
                    gc.playerList.mapNotNull { it.name }.filter { it.lowercase().startsWith(current) }
                }

                // Show all players
                "add", "create" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(current) }

                else -> emptyList()
            }

            else -> emptyList()
        }
    }
}