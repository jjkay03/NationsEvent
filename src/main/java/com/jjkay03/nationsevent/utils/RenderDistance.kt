package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RenderDistance: Listener {
    private val config = NationsEvent.INSTANCE.config
    private val defaultPlayersRenderDistance: Int = config.getInt("default-players-render-distance")

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.hasPermission(NationsEvent.PERM_ADMIN)) return
        event.player.sendViewDistance = defaultPlayersRenderDistance

    }

}