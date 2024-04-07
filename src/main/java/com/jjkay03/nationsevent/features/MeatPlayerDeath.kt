package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

class MeatPlayerDeath : Listener {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("feature-meat-player-death")

    private val minMeatAmount = 8
    private val maxMeatAmount = 12

    // Create the suspicious meat item
    private val suspiciousMeat: ItemStack = ItemStack(Material.BEEF).apply {
        amount = (minMeatAmount..maxMeatAmount).random() // Random amount between min and max
        itemMeta = itemMeta?.apply { setDisplayName("§fSuspicious Meat") }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (featureEnabled) {
            val player = event.entity as? Player ?: return
            val dropLocation = player.location

            // Drop suspicious meat at the player's death location
            dropLocation.world.dropItem(dropLocation, suspiciousMeat)
        }
    }
}
