package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.world.StructureGrowEvent
import kotlin.random.Random

class IslandColdPlants : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.Companion.INSTANCE)
    }

    // Crops grow slower (25% slower)
    @EventHandler
    fun onCropGrow(event: BlockGrowEvent) {
        if (EventSpecific.WORLD_NS8_COLD != event.block.world) return
        if (Random.nextInt(4) == 0) event.isCancelled = true
    }

    // Trees grow slower (50% slower)
    @EventHandler
    fun onTreeGrow(event: StructureGrowEvent) {
        if (EventSpecific.WORLD_NS8_COLD != event.world) return
        if (Random.nextBoolean()) event.isCancelled = true
    }
}
