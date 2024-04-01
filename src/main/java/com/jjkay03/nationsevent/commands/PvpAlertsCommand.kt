package com.jjkay03.nationsevent.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PvpAlertsCommand: CommandExecutor {

    private val alertedPlayers = mutableSetOf<String>()

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        val playerUUID = sender.uniqueId.toString()

        // Add or remove player from list
        if (alertedPlayers.contains(playerUUID)) {
            alertedPlayers.remove(playerUUID)
            sender.sendMessage("§7\uD83D\uDD14 You have §cDISABLED §7PVP alerts")
        } else {
            alertedPlayers.add(playerUUID)
            sender.sendMessage("§7\uD83D\uDD14 You have §aENABLED §7PVP alerts")
        }

        return true
    }
}