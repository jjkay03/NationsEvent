package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityBreedEvent
import kotlin.random.Random

class IslandColdMobs : Listener {

    companion object {
        val BLOCKED_MOBS = listOf(
            EntityType.COW,
            EntityType.SHEEP,
            EntityType.PIG,
            EntityType.HORSE
        )
    }

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.Companion.INSTANCE)
    }

    // Block certain mobs from spawning
    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (EventSpecific.WORLD_NS8_COLD != event.entity.world) return
        if (event.entityType in BLOCKED_MOBS) event.isCancelled = true
    }

    // 75% chance breeding fails
    @EventHandler
    fun onBreed(event: EntityBreedEvent) {
        if (EventSpecific.WORLD_NS8_COLD != event.entity.world) return
        if (Random.nextInt(4) != 0) { // 3/4 chance = 75% fail
            event.isCancelled = true
            val loc = event.entity.location.add(0.0, 0.5, 0.0)
            loc.world?.spawnParticle(Particle.SMOKE, loc, 10, 0.3, 0.3, 0.3, 0.02)
            loc.world?.playSound(loc, Sound.ENTITY_VILLAGER_NO, 0.8f, 1.2f)
        }
    }
}
