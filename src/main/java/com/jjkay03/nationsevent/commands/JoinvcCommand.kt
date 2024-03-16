package com.jjkay03.nationsevent.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinvcCommand: CommandExecutor {
    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // End if command doesn't have the right number of args
        if (args.size != 1) { sender.sendMessage("§cUsage: /joinvc <player>"); return true }

        // Get player
        val targetPlayer: Player? = Bukkit.getPlayer(args[0])

        // End if player is not found
        if (targetPlayer == null || !targetPlayer.isOnline) { sender.sendMessage("§cPlayer not found or not online."); return true }

        // Send message to target
        targetPlayer.sendTitle("§a\uD83D\uDD0A", "§aJoin \"Waiting for staff\" VC!", 10, 80, 10)
        targetPlayer.playSound(targetPlayer.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
        targetPlayer.sendMessage("§a\uD83D\uDD0A Join \"Waiting for staff\" VC!")

        // Send confirmation message to command sender
        sender.sendMessage("§7\uD83D\uDD0A Sent VC invitation to ${targetPlayer.name}")

        return true
    }
}