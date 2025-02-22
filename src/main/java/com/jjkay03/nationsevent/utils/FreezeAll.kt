package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.Saves
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

    // List of bypass permissions
    private val bypassPermissions = listOf(Saves.PERM_STAFF, Saves.PERM_SPECTATOR)
    private val frozenMessage = "§cAll players are frozen"

    // Helper function to check if a player has any of the bypass permissions
    private fun Player.hasAnyPermission(permissions: List<String>): Boolean {
        return permissions.any { this.hasPermission(it) }
    }

    // Event to handle player movement
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // End if freeze is off or player has bypass perm
        if (!FREEZE_ALL_ENABLED || event.player.hasAnyPermission(bypassPermissions)) return
        val from = event.from
        val to = event.to
        if (to.y < from.y) return // Allow the player to fall
        // Prevent all other movement (X and Z axes)
        if (from.x != to.x || from.z != to.z || from.y != to.y) {
            event.to = from.setDirection(to.direction) // Preserve yaw/pitch, but reset position
            event.player.sendMessage("$frozenMessage - You can't move!")
        }
    }

    // Event to handle block breaking
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        // End if freeze is off or player has bypass perm
        if (!FREEZE_ALL_ENABLED || event.player.hasAnyPermission(bypassPermissions)) return
        // Cancel block break
        event.isCancelled = true
        event.player.sendMessage("$frozenMessage - You can't break!")
    }

    // Event to handle block placing
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        // End if freeze is off or player has bypass perm
        if (!FREEZE_ALL_ENABLED || event.player.hasAnyPermission(bypassPermissions)) return
        // Cancel block place
        event.isCancelled = true
        event.player.sendMessage("$frozenMessage - You can't place!")
    }

    // Event to handle entity damage
    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (!FREEZE_ALL_ENABLED || event.entity !is Player) return
        val player = event.entity as Player
        if (player.hasAnyPermission(bypassPermissions)) return
        // Cancel the damage event
        event.isCancelled = true
        player.sendMessage("$frozenMessage - You are protected from damage!")
    }
}