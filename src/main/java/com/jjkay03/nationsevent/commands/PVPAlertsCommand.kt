package com.jjkay03.nationsevent.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PVPAlertsCommand: CommandExecutor {

    companion object {
        val PVP_ALERTS_PLAYERS = mutableSetOf<String>()
    }

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        val playerUUID = sender.uniqueId.toString()

        // Add or remove player from list
        if (PVP_ALERTS_PLAYERS.contains(playerUUID)) {
            PVP_ALERTS_PLAYERS.remove(playerUUID)
            sender.sendMessage("§7\uD83D\uDD14 You have §cDISABLED §7PVP alerts")
        } else {
            PVP_ALERTS_PLAYERS.add(playerUUID)
            sender.sendMessage("§7\uD83D\uDD14 You have §aENABLED §7PVP alerts")
        }

        return true
    }
}