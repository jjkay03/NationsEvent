package com.jjkay03.nationsevent.specific.island_both

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

class IslandBothFishing : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    // List of possible loot items
    private val lootItems = listOf(
        Material.STICK,
        Material.LILY_PAD,
        Material.CLAY_BALL,
        Material.ROTTEN_FLESH,
        Material.BONE,
        Material.STRING,
        Material.LEATHER,
        Material.FEATHER,
        Material.BRICKS,
        Material.WHEAT_SEEDS,
        Material.KELP,
        Material.SEAGRASS
    )

    // Replace fish catches with random loot
    @EventHandler
    fun onFish(event: PlayerFishEvent) {
        // Only handle when a fish is actually caught
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return

        // End if not on hot or cold island
        val world = event.player.world
        if (world != EventSpecific.WORLD_NS8_HOT && world != EventSpecific.WORLD_NS8_COLD) return

        // Get the caught item
        val caught = event.caught as? Item ?: return
        val caughtItem = caught.itemStack

        // Check if it's a fish we want to replace
        val fishTypes = setOf(
            Material.COD,
            Material.SALMON,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH
        )

        if (caughtItem.type in fishTypes) {
            // Replace with random loot
            val randomLoot = lootItems.random()
            caught.itemStack = ItemStack(randomLoot, 1)
        }
    }
}