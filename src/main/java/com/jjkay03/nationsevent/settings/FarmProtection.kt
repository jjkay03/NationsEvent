package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent

class FarmProtection: Listener {

    // INITIALIZATION
    init {
        load(Config.SETTINGS_FARM_PROTECTION)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
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