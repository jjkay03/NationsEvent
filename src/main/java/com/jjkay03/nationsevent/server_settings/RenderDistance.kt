package com.jjkay03.nationsevent.server_settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RenderDistance: Listener {
    // REGISTER IF ENABLED
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("render-distance-manager")
    init { if (featureEnabled) Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }

    // Variables
    private val renderDistanceDefaultPlayers: Int = config.getInt("render-distance-default-players")

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.hasPermission(Saves.PERM_ADMIN)) return
        event.player.sendViewDistance = renderDistanceDefaultPlayers

    }

}