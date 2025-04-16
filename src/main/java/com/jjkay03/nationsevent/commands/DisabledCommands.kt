package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.Saves
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class DisabledCommands(private val featureName: String) : CommandExecutor {

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.hasPermission(Saves.PERM_ADMIN)) sender.sendMessage("§c${featureName} is disabled in config!")
        else sender.sendMessage("§c${featureName} is disabled!")
        return true
    }

}