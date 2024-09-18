package com.jjkay03.nationsevent.commands.playerscale

import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class PlayerScaleRestAllCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // Check if the correct confirmation is provided
        if (args.isEmpty() || args[0] != "CONFIRM") {
            sender.sendMessage("§cTo reset all players scales, type: /playerscalerestall CONFIRM")
            return true
        }

        // Iterate through all online players
        for (player in Bukkit.getOnlinePlayers()) {
            // Get the player's scale attribute
            val scaleAttribute = player.getAttribute(Attribute.GENERIC_SCALE)

            // Reset the player's scale to 1 if the attribute is available
            if (scaleAttribute != null) scaleAttribute.baseValue = 1.0
        }

        // Notify the command sender that all players' scales have been reset
        sender.sendMessage("§6All players' scales have been reset to default (1)")

        return true
    }
}