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

class PVPToggle : Listener {

    companion object {
        var PVP_ENABLED = false
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
