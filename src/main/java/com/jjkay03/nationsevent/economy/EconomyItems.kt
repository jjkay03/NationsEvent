package com.jjkay03.nationsevent.economy

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack

class EconomyItems : Listener {

    // Event handler for when an item is picked up
    @EventHandler
    fun onItemPickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        val itemStack = event.item.itemStack
        event.item.itemStack = addValueToLore(itemStack)
    }

    // Event handler for when a player switches the item in their main hand
    @EventHandler
    fun onItemHeld(event: PlayerItemHeldEvent) {
        val player = event.player
        val item = player.inventory.getItem(event.newSlot) ?: return
        player.inventory.setItem(event.newSlot, addValueToLore(item))
    }

    // Event handler for when a player interacts with an item
    @EventHandler
    fun onItemInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        event.player.inventory.setItemInMainHand(addValueToLore(item))
    }

    // Event handler for when an item is crafted
    @EventHandler
    fun onItemCraft(event: PrepareItemCraftEvent) {
        val result = event.inventory.result ?: return
        event.inventory.result = addValueToLore(result.clone())
    }

    // Function to add item value to lore
    private fun addValueToLore(item: ItemStack): ItemStack {
        val meta = item.itemMeta ?: return item
        val economyItem = EconomyItemsValues.entries.find { it.item == item.type } ?: return item
        val lore = meta.lore ?: mutableListOf()

        // Remove any existing money value entries
        val filteredLore = lore.filterNot { line ->
            EconomyItemsValues.entries.any { line == EconomyUtils.formatMoney(it.value) }
        }.toMutableList()

        // Add the updated money value
        filteredLore.add(EconomyUtils.formatMoney(economyItem.value))
        meta.lore = filteredLore
        item.itemMeta = meta

        return item
    }

    // TODO - Code selling items function

}