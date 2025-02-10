package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack

class LimitEnchant : Listener {

    // REGISTER IF ENABLED IN CONFIG
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("setting-limit-enchant")
    init { if (featureEnabled) Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE) }

    // Check item when held
    @EventHandler
    fun onItemHeld(event: PlayerItemHeldEvent) {
        checkItem(event.player.inventory.itemInMainHand, event.player)
    }

    // Check item when interact
    @EventHandler
    fun onItemInteract(event: PlayerInteractEvent) {
        checkItem(event.player.inventory.itemInMainHand, event.player)
        checkArmorSlotsItems(event.player) // Check player armor slots
    }

    // Function to adjust enchantments on an item based on allowed levels
    private fun adjustEnchantments(item: ItemStack, player: Player) {
        for ((enchantment, level) in item.enchantments) {
            val allowedLevel = Saves.LIMITED_ENCHANTMENTS[enchantment] ?: continue

            // If the enchantment level is greater than allowed, adjust it
            if (level > allowedLevel) {
                if (allowedLevel == 0) {
                    // Remove the enchantment if the allowed level is 0
                    item.removeEnchantment(enchantment)
                    player.sendMessage("§8✖ Remove disabled enchant ${enchantment.key.key.uppercase()} from ${item.type.name}")
                } else {
                    // Set the enchantment to the allowed level
                    item.removeEnchantment(enchantment)
                    item.addEnchantment(enchantment, allowedLevel)
                    player.sendMessage("§8✖ Adjusted limited enchant ${enchantment.key.key.uppercase()} (max level ${allowedLevel}) on ${item.type.name}")
                }
            }
        }
    }

    // Check if item is not air and if item is enchanted before running enchant checks
    private fun checkItem(item: ItemStack, player: Player) {
        if (item.type == Material.AIR) return
        if (item.enchantments.isEmpty()) return
        adjustEnchantments(item, player)
    }

    // Check armor slots
    private fun checkArmorSlotsItems(player: Player) {
        // Get armor slots items
        val armorItems = listOf(
            player.inventory.helmet,
            player.inventory.chestplate,
            player.inventory.leggings,
            player.inventory.boots
        )

        // Loop through each armor item and adjust enchantments
        for (armorItem in armorItems) {
            if (armorItem != null && armorItem.type != Material.AIR) {
                adjustEnchantments(armorItem, player)
            }
        }
    }
}