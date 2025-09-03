package com.jjkay03.nationsevent.server_settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RenderDistance: Listener {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("render-distance-manager")
    private val renderDistanceDefaultPlayers: Int = config.getInt("render-distance-default-players")

    // REGISTER IF ENABLED
    init {
        if (featureEnabled) Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.hasPermission(Saves.PERM_ADMIN)) return
        event.player.sendViewDistance = renderDistanceDefaultPlayers

    }

}