package com.jjkay03.nationsevent.specific.ng6.commands

import com.jjkay03.nationsevent.Saves
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class NG6_ParkourCiv : Listener {

    private val parkourCivWorldName = "nations_ng6_pc_world"

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = player.location

        // End if player is staff
        if (player.hasPermission(Saves.PERM_STAFF)) return

        // End if player not in parkour civ world
        if (location.world?.name != parkourCivWorldName) return

        // End if player is above Y 20
        if (location.y > 20) return

        // If player food level more than 6 (3 bars) set it back to 6
        if (player.foodLevel > 6) player.foodLevel = 6
    }

}