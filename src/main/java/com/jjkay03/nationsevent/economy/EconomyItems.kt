package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.LogsManager
import org.bukkit.Material
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

    companion object {

        // Function to sell items in player hand if they are economy items
        fun sellPlayerItems(player: Player) : Long {
            // Checks
            val playerItem = player.inventory.itemInMainHand
            if (playerItem.type == Material.AIR) return 0
            val economyItem = EconomyItemsValues.entries.find { it.item == playerItem.type } ?: run { player.sendMessage("§cThis item is not for sale!"); return 0 }

            // Operations
            val itemAmount = playerItem.amount.toLong()
            val totalPrice = economyItem.value * itemAmount
            val playerBalance = EconomyUtils.getPlayerBalance(player)
            val playerUpdatedBalance = playerBalance + totalPrice
            EconomyUtils.setPlayerBalance(player, playerUpdatedBalance)

            // Delete items
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))

            // Alert player and log
            player.sendMessage("§a[${Economy.MONEY_SYMBOL}➕] §7You received ${EconomyUtils.formatMoney(totalPrice)} §7from selling ${playerItem.type} x${itemAmount} §7(new bal ${EconomyUtils.formatMoney(playerUpdatedBalance)}§7)")
            LogsManager.log(Saves.LOG_FILE_ECONOMY, "Economy", "[ITEM SALE] ${player.name} ([+] $playerBalance -> $playerUpdatedBalance) sold ${playerItem.type} x${itemAmount} for $totalPrice")

            // Update player stats
            EconomyUtils.updatePlayerBalanceFileKeyLong(player, itemAmount, setOf(Economy.KEY_SOLD_ITEMS, Economy.KEY_SOLD_ITEMS_SLT))
            EconomyUtils.updatePlayerBalanceFileKeyLong(player, totalPrice, setOf(Economy.KEY_SOLD_ITEMS_PROFIT_SLT, Economy.KEY_PROFIT_SLT))

            return totalPrice

        }

    }

}