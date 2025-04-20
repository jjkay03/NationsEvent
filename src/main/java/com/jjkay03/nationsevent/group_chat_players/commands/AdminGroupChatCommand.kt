package com.jjkay03.nationsevent.group_chat_players.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class AdminGroupChatCommand : CommandExecutor, TabCompleter {

    /*

    ❌ /agc coords <gc>
    ❌ /agc chat <gc> message
    ❌ /agc create <name> <owner> <players...>
    ❌ /agc delete <ID>
    ❌ /agc add <ID> <players...>
    ❌ /agc remove <ID> <players...>
    ❌ /agc list <player / @a>
    ❌ /agc setowner <ID> <player>
    ❌ /agc spy <ID / @a>

     */

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // TODO
        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        // TODO
        return listOf()
    }
}