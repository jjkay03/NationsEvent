package com.jjkay03.nationsevent.group_chat_players.commands

import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatManager.Companion.GROUP_CHATS
import com.jjkay03.nationsevent.group_chat_players.PlayerGroupChatUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
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

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Checks
        if (sender is ConsoleCommandSender) {sender.sendMessage("§eOnly players can use this command!"); return true;}
        if (args.isEmpty()) {sender.sendMessage("§cUsage: /$label <coords/chat/create/delete/invite/join/leave/select/setowner>"); return true;}

        val player = sender as Player

        // Deal with arguments
        when (args[0].lowercase()) {
            // TODO
        }

        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (sender !is Player) return emptyList()
        val sub = args.getOrNull(0)?.lowercase() ?: return emptyList()
        val current = args.last().lowercase()
        val options = listOf("coords", "chat", "create", "delete", "add", "remove", "list", "setowner", "spy")
        val player = sender

        return when (args.size) {
            // First argument - command list
            1 -> options.filter { it.startsWith(sub, true) }

            // 2nd argument - context-specific completions
            2 -> when (sub) {
                "coords", "chat", "delete", "add", "remove", "setowner" ->
                    PlayerGroupChatUtils.tabCompletePlayerGCsList(GROUP_CHATS).filter { it.lowercase().startsWith(current) }

                "spy" -> (listOf("@a") + PlayerGroupChatUtils.tabCompletePlayerGCsList(GROUP_CHATS)).filter { it.lowercase().startsWith(current) }

                "list" -> (listOf("@a") + Bukkit.getOnlinePlayers().map { it.name }).filter { it.lowercase().startsWith(current) }

                "create" -> listOf("<name>").filter { it.startsWith(current, true) }

                else -> emptyList()
            }

            // 3rd argument - context-specific completions
            3 -> when (sub) {

                "remove", "setowner" -> {
                    val gc = PlayerGroupChatUtils.tabCompleteInputGCGet(args[1]) ?: return emptyList()
                    gc.playerList.mapNotNull { it.name }.filter { it.lowercase().startsWith(current) }
                }

                "add", "create" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(current) }

                else -> emptyList()
            }

            // 4th+ argument - context-specific completions
            in 4..Int.MAX_VALUE -> when (sub) {

                "remove" -> {
                    val gc = PlayerGroupChatUtils.tabCompleteInputGCGet(args[1]) ?: return emptyList()
                    gc.playerList.mapNotNull { it.name }.filter { it.lowercase().startsWith(current) }
                }

                "add", "create" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(current) }

                else -> emptyList()
            }

            else -> emptyList()
        }
    }
}