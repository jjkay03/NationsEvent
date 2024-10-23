package com.jjkay03.nationsevent.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FullMoonCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if sender not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        val player = sender as Player
        val world = player.world

        // Calculate the full moon phase (must occur every 8 days)
        // Set the total world time such that it corresponds to a full moon
        val currentDay = world.fullTime / 24000
        val daysUntilFullMoon = (8 - (currentDay % 8)) % 8
        val newDay = currentDay + daysUntilFullMoon

        // Set the world's full time to the start of that full moon day (210000 for the night phase)
        world.fullTime = newDay * 24000 + 18000 // 18000 is for middle of the night

        player.sendMessage("§6Time set to full moon 🌕")

        return true
    }
}
