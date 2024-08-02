package com.jjkay03.nationsevent.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BypassViewDistanceCommand: CommandExecutor {

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // Check if sender is player
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be ran by players!")
            return true
        }

        // Change sender view distance
        if (sender.sendViewDistance != 32) {
            sender.sendViewDistance = 32
            sender.sendMessage("§aYou are now bypassing the server max view distance (32)")
        } else {
            sender.sendViewDistance = -1
            sender.sendMessage("§aYou are now respecting the server max view distance")
        }

        return true
    }
}