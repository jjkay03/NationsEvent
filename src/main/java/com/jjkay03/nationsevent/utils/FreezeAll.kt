package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.entity.Player

class FreezeAll : Listener {

    companion object {
        var FREEZE_ALL_ENABLED = false
    }

    private val bypassPermission = NationsEvent.PERM_STAFF
    private val frozenMessage = "§cAll players are frozen"

    // Event to handle player movement
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (FREEZE_ALL_ENABLED && !event.player.hasPermission(bypassPermission)) {
            // Allow the player to look around but prevent movement by checking only X, Y, Z coordinates
            if (event.from.x != event.to.x || event.from.y != event.to.y || event.from.z != event.to.z) {
                event.to = event.from.setDirection(event.to.direction) // Preserve yaw/pitch, but reset position
                event.player.sendMessage("$frozenMessage - You can't move!")
            }
        }
    }

    // Event to handle block breaking
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (FREEZE_ALL_ENABLED && !event.player.hasPermission(bypassPermission)) {
            event.isCancelled = true
            event.player.sendMessage("$frozenMessage - You can't break!")
        }
    }

    // Event to handle block placing
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (FREEZE_ALL_ENABLED && !event.player.hasPermission(bypassPermission)) {
            event.isCancelled = true
            event.player.sendMessage("$frozenMessage - You can't place!")
        }
    }

    // Event to handle entity damage
    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        // Check if the entity is a player and if they are frozen
        if (FREEZE_ALL_ENABLED && event.entity is Player) {
            val player = event.entity as Player
            if (!player.hasPermission(bypassPermission)) {
                // Cancel the damage event
                event.isCancelled = true
                player.sendMessage("$frozenMessage - You are protected from damage!")
            }
        }
    }
}
