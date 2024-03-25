package com.jjkay03.nationsevent.utils

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.java.JavaPlugin

class PVPToggle(private val plugin: JavaPlugin) : CommandExecutor, Listener {
    private val config = plugin.config
    private var pvpEnabled: Boolean = config.getBoolean("pvp-enabled")

    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        pvpEnabled = !pvpEnabled // Toggle PVP state
        config.set("pvp-enabled", pvpEnabled) // Update config value
        plugin.saveConfig() // Save the updated config

        // Notify all players on the server
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.sendMessage(
                if (pvpEnabled) "§a\uD83D\uDDE1 PVP has been enabled!"
                else "§c\uD83D\uDDE1 PVP has been disabled!"
            )
        }

        return true
    }

    // Hits
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        // End if PVP is enabled
        if (pvpEnabled) {return}

        // Check if the damage was caused by a player to another player
        if (event.damager is Player && event.entity is Player) {
            event.isCancelled = true
        }
    }

    // Arrows
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        // End if PVP is enabled
        if (pvpEnabled) {return}

        // Check if the damage was caused by an arrow from a player to another player
        if (event.cause == EntityDamageEvent.DamageCause.PROJECTILE && event.entity is Player && event.entity is Player) {
            event.isCancelled = true
        }
    }
}
