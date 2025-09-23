package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class LimitEnchants : Listener {

    // INITIALIZATION
    init {
        load(Config.SETTINGS_LIMIT_ENCHANTS)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    // Check item when held
    @EventHandler
    fun onItemHeld(event: PlayerItemHeldEvent) {
        checkItem(event.player.inventory.itemInMainHand, event.player)
    }

    // Check item when interact
    @EventHandler
    fun onItemInteract(event: PlayerInteractEvent) {
        checkItem(event.player.inventory.itemInMainHand, event.player) // Check main hand
        checkItem(event.player.inventory.itemInOffHand, event.player) // Check off-hand
        checkArmorSlotsItems(event.player) // Check player armor slots
    }

    // Check when swapping hand items
    @EventHandler
    fun onSwapHandItems(event: PlayerSwapHandItemsEvent) {
        checkItem(event.mainHandItem, event.player) // Check main hand
        checkItem(event.offHandItem, event.player) // Check off-hand
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

    // Helper function to check if item is not air and if item is enchanted before running enchant checks
    private fun checkItem(item: ItemStack, player: Player) {
        if (item.type == Material.AIR) return
        if (item.enchantments.isEmpty()) return
        adjustEnchantments(item, player)
    }

    // Helper function to check armor slots
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