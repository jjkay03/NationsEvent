package com.jjkay03.nationsevent.specific.island_hot

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Animals
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.player.PlayerEggThrowEvent
import kotlin.random.Random

class IslandHotMob : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
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

    // Prevent animals from laying eggs (chicken, turtle, sniffer)
    @EventHandler
    fun onAnimalLayEgg(event: EntityDropItemEvent) {
        if (EventSpecific.WORLD_NS8_HOT != event.entity.world) return

        val itemType = event.itemDrop.itemStack.type
        if (itemType != Material.EGG &&
            itemType != Material.BROWN_EGG &&
            itemType != Material.BLUE_EGG) return

        event.isCancelled = true
    }

    // Prevent eggs from spawning chickens
    @EventHandler
    fun onEggThrow(event: PlayerEggThrowEvent) {
        if (EventSpecific.WORLD_NS8_HOT != event.player.world) return
        event.isHatching = false
    }
}
