package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.Saves
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent

class NoCraft : Listener {
    // Replace item result with air
    @EventHandler
    fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        val result = event.inventory.result ?: return
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.inventory.result = null
    }

    // Cancel craft
    @EventHandler
    fun onCraftItem(event: CraftItemEvent) {
        val result = event.currentItem ?: return
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.isCancelled = true
        val player = event.whoClicked
        player.sendMessage("§8✖ Item ${result.type.name} craft is disabled!")
    }

    // Cancel craft from crafter
    @EventHandler
    fun onCrafterCraft(event: CrafterCraftEvent) {
        val result = event.result
        if (result.type !in Saves.DISABLED_CRAFT_ITEMS) return
        event.isCancelled = true

        // Notify nearby players
        event.block.world.getNearbyEntities(event.block.location, 4.0, 4.0, 4.0).forEach {
            entity ->  if (entity is Player) entity.sendMessage("§8✖ Item ${result.type.name} craft is disabled (crafter)!")
        }
    }
}