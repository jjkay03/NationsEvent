package com.jjkay03.nationsevent.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinStageCommand: CommandExecutor {
    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // Send a title to all online players
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendTitle("§a\uD83D\uDD0A", "§aJoin stage channel!", 10, 100, 10)
            player.sendMessage("§a\uD83D\uDD0A Join stage channel!")
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
        }

        return true
    }
}