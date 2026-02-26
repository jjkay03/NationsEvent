package com.jjkay03.nationsevent.specific.island_hot

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Particle
import org.bukkit.entity.Animals
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import kotlin.random.Random

class IslandHotMob : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.Companion.INSTANCE)
    }

    // 50% chance breeding fails + 2x breeding cooldown
    @EventHandler
    fun onBreed(event: EntityBreedEvent) {
        if (EventSpecific.WORLD_NS8_HOT != event.entity.world) return

        // Double breeding cooldown (default 6000 ticks -> 12000)
        if (event.mother is Animals) (event.mother as Animals).age = 12000
        if (event.father is Animals) (event.father as Animals).age = 12000

        // 50% chance breeding fails
        if (Random.nextBoolean()) {
            event.isCancelled = true
            val loc = event.entity.location.add(0.0, 0.5, 0.0)
            loc.world?.spawnParticle(Particle.SMOKE, loc, 10, 0.3, 0.3, 0.3, 0.02)
        }
    }
}
