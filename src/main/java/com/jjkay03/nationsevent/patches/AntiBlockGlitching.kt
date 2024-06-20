package com.jjkay03.nationsevent.patches

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.BoundingBox
import java.util.*

class AntiBlockGlitching: Listener {

    // REGISTER IF ENABLED
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("patch-anti-block-glitching")
    init { if (featureEnabled) Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }

    // Variables
    private val lastLocationMap: MutableMap<UUID, Location> = mutableMapOf()

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!event.isCancelled) return
        handleEvent(event.player, event.blockPlaced)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBucketUse(event: PlayerBucketEmptyEvent) {
        if (!event.isCancelled) return
        handleEvent(event.player, event.block)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerMove(event: PlayerMoveEvent) {
        // Update the player's last location
        lastLocationMap[event.player.uniqueId] = event.to
    }

    private fun handleEvent(player: Player, block: Block) {
        val lastLocation = lastLocationMap[player.uniqueId]
        if (lastLocation != null) player.teleport(lastLocation)
    }
}