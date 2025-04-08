package com.jjkay03.nationsevent.economy.commands

import com.jjkay03.nationsevent.Saves
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class DisabledEconomyCommands : CommandExecutor {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.hasPermission(Saves.PERM_ADMIN)) sender.sendMessage("§cEconomy system is disabled in config!")
        else sender.sendMessage("§cEconomy system is disabled!")
        return true
    }

}