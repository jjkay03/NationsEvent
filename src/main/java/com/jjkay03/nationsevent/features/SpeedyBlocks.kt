package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SpeedyBlocks : Listener {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("feature-speedy-blocks")

    // Configuration
    private val listOfSpeedyBlocks: Array<Material> = arrayOf(Material.PACKED_MUD, Material.MUD_BRICKS)
    private val speedyEffect: PotionEffect = PotionEffect(PotionEffectType.SPEED, 40, 0, false, false, false)
    private val speedyTimeRange: Pair<Long, Long> = Pair(3000, 11000)

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // Stop if feature disabled
        if (!featureEnabled) return

        val player = event.player
        val timeOfDay = player.world.time

        // Check if current time is within the defined speedy time range
        if (timeOfDay !in speedyTimeRange.first..speedyTimeRange.second) return

        // Get the block beneath the player's feet
        val blockUnderPlayer = player.location.add(0.0, -1.0, 0.0).block

        // If the block is in the list of speedy blocks, apply the speed effect
        if (blockUnderPlayer.type in listOfSpeedyBlocks) player.addPotionEffect(speedyEffect)
    }
}
