package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent

class Feature_FarmProtection: Listener {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("feature-farm-protection")

    // REGISTER IF ENABLED
    init {
        if (featureEnabled) { Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }
    }

    // Protect from mobs
    @EventHandler
    fun onEntityInteract(event: EntityInteractEvent) {
        val block: Block = event.block
        if (block.type == Material.FARMLAND) {
            event.isCancelled = true
        }
    }

    // Protect from players
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.PHYSICAL) {
            val clickedBlock = event.clickedBlock ?: return
            if (clickedBlock.type == Material.FARMLAND) {
                event.isCancelled = true
            }
        }
    }
}