package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.utils.PVPToggle
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class PVPToggleCommand: CommandExecutor, TabCompleter {

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        var silent = false
        PVPToggle.PVP_ENABLED = !PVPToggle.PVP_ENABLED // Toggle PVP state

        // Check if the first argument is "silent"
        if (args.isNotEmpty() && args[0].toLowerCase() in setOf("silent", "s")) { silent = true }

        // Notify all players on the server if not silent
        if (!silent) {
            Bukkit.getServer().onlinePlayers.forEach { player ->
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
                player.sendMessage(
                    if (PVPToggle.PVP_ENABLED) "§a\uD83D\uDDE1 PVP has been ENABLED!"
                    else "§c\uD83D\uDDE1 PVP has been DISABLED!"
                )
            }
        } else {
            sender.sendMessage(
                if (PVPToggle.PVP_ENABLED) "§7\uD83D\uDDE1 PVP has been §aENABLED §7silently"
                else "§7\uD83D\uDDE1 PVP has been §cDISABLED §7silently"
            )
        }

        return true
    }

    // Tab complete
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("silent")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }
}