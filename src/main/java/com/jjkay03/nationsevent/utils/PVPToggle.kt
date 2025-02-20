package com.jjkay03.nationsevent.utils

import org.bukkit.entity.Arrow
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
        if (PVP_ENABLED) return // End if PVP is off
        if (event.damager is Player && event.entity is Player) event.isCancelled = true
    }

    // Arrows
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (PVP_ENABLED) return // End if PVP is off
        val damaged = event.entity as? Player ?: return // End if damaged entity isn't player
        val projectile = event.damager as? Arrow ?: return // End if damage not caused by arrow
        val shooter = projectile.shooter as? Player ?: return // End if arrow not shot by player
        event.isCancelled = true
    }

}
