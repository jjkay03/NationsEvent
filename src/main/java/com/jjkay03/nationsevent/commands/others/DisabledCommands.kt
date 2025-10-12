package com.jjkay03.nationsevent.commands.others

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class DisabledCommands(private val message: String) : CommandExecutor {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("§c${message}")
        return true
    }

}