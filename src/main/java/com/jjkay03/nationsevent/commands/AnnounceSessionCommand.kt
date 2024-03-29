package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AnnounceSessionCommand: CommandExecutor {
    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // End if command doesn't have the right number of args
        if (args.size != 1) { sender.sendMessage("§cUsage: /announcesession <number>"); return true }

        // End if arg is not an Int
        val sessionNumber: Int = try { args[0].toInt() } catch (e: NumberFormatException) { sender.sendMessage("§cInvalid session number!"); return true }

        // Send a title to all online players
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendTitle("§6SESSION $sessionNumber", "§eStart now!", 10, 100, 10)
            player.sendMessage("§6\uD83D\uDD14 Session $sessionNumber has started!")
            player.playSound(player.location, Sound.BLOCK_END_PORTAL_SPAWN, 1f, 1f)
        }

        // Set session start time
        NationsEvent.SESSION_START_TIME = System.currentTimeMillis()

        return true
    }
}