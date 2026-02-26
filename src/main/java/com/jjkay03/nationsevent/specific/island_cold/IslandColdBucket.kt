package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class IslandColdBucket : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.Companion.INSTANCE)
    }

    // Water bucket freezing 50/50 chance
    @EventHandler
    fun onWaterPlace(event: PlayerBucketEmptyEvent) {
        // End if not on cold island
        if (EventSpecific.WORLD_NS8_COLD != event.block.world) return

        // End if not water bucket
        if (event.bucket != Material.WATER_BUCKET) return

        // 50/50 chance
        if (Random.nextBoolean()) return

        // Get block and location
        val block = event.block
        val location = block.location.add(0.5, 0.5, 0.5)

        // Cancel the water placement
        event.isCancelled = true

        // Empty the bucket anyway
        event.player.inventory.setItem(event.hand, ItemStack(Material.BUCKET))

        // Set block to powdered snow
        block.type = Material.POWDER_SNOW

        // Play freezing sound and particles
        block.world.playSound(location, Sound.BLOCK_GLASS_PLACE, 1.0f, 1.5f)
        block.world.spawnParticle(Particle.SNOWFLAKE, location, 12, 0.3, 0.3, 0.3, 0.02)
    }

}