package com.jjkay03.nationsevent.utils

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class PVPToggle : CommandExecutor, TabCompleter, Listener {

    companion object {
        var PVP_ENABLED = false
    }

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        var silent = false
        PVP_ENABLED = !PVP_ENABLED // Toggle PVP state

        // Check if the first argument is "silent"
        if (args.isNotEmpty() && args[0].toLowerCase() in setOf("silent", "s")) {
            silent = true
        }

        // Notify all players on the server if not silent
        if (!silent) {
            Bukkit.getServer().onlinePlayers.forEach { player ->
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
                player.sendMessage(
                    if (PVP_ENABLED) "§a\uD83D\uDDE1 PVP has been ENABLED!"
                    else "§c\uD83D\uDDE1 PVP has been DISABLED!"
                )
            }
        }
        else {
            sender.sendMessage(
                if (PVP_ENABLED) "§7\uD83D\uDDE1 PVP has been ENABLED silently!"
                else "§7\uD83D\uDDE1 PVP has been DISABLED silently!"
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

    // Hits
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        // End if PVP is enabled
        if (PVP_ENABLED) {return}

        // Check if the damage was caused by a player to another player
        if (event.damager is Player && event.entity is Player) {
            event.isCancelled = true
        }
    }

    // Arrows
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        // End if PVP is enabled
        if (PVP_ENABLED) {return}

        // Check if the damage was caused by an arrow from a player to another player
        if (event.cause == EntityDamageEvent.DamageCause.PROJECTILE && event.entity is Player && event.entity is Player) {
            event.isCancelled = true
        }
    }
}
