package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.world.StructureGrowEvent
import kotlin.random.Random

class IslandColdPlants : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.Companion.INSTANCE)
    }

    // Crops grow slower (25% slower), or not at all during thunderstorms
    @EventHandler
    fun onCropGrow(event: BlockGrowEvent) {
        val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return
        if (coldWorld != event.block.world) return

        // Cancel all growth during thunderstorms
        if (coldWorld.isThundering) {
            event.isCancelled = true
            return
        }

        // Otherwise, 25% slower growth
        if (Random.nextInt(4) == 0) event.isCancelled = true
    }

    // Trees grow slower (50% slower), or not at all during thunderstorms
    @EventHandler
    fun onTreeGrow(event: StructureGrowEvent) {
        val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return
        if (coldWorld != event.world) return

        // Cancel all growth during thunderstorms
        if (coldWorld.isThundering) {
            event.isCancelled = true
            return
        }

        // Otherwise, 50% slower growth
        if (Random.nextBoolean()) event.isCancelled = true
    }

    // Crops don't drop food during thunderstorms
    @EventHandler
    fun onCropBreak(event: BlockBreakEvent) {
        val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return
        if (coldWorld != event.block.world) return

        // End if not thundering
        if (!coldWorld.isThundering) return

        // Remove all food drops from crop blocks
        val block = event.block
        if (isCrop(block.type)) {
            event.isDropItems = false
        }
    }

    // Check if a material is a crop
    private fun isCrop(material: Material): Boolean {
        return when (material) {
            Material.WHEAT, Material.CARROTS, Material.POTATOES,
            Material.BEETROOTS, Material.SWEET_BERRY_BUSH,
            Material.COCOA, Material.MELON, Material.PUMPKIN -> true
            else -> false
        }
    }
}
