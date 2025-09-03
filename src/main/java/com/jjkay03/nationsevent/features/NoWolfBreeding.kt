package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent

class NoWolfBreeding : Listener {
    private val config = NationsEvent.INSTANCE.config
    private val featureEnabled: Boolean = config.getBoolean("feature-no-wolf-breeding")

    @EventHandler
    fun onEntityBreed(event: EntityBreedEvent) {
        // Stop if feature disabled
        if (!featureEnabled) return

        // If the entity being bred is a wolf, cancel breeding
        if (event.entity.type == EntityType.WOLF) {
            event.isCancelled = true

            // If a player caused the breeding, notify them
            val breeder = event.breeder
            if (breeder is Player) {
                breeder.sendMessage("§cWolf breeding is disabled!")
            }
        }
    }
}
