package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent

class DisableWolfBreeding : Listener {

    // REGISTER EVENTS
    init {
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    @EventHandler
    fun onEntityBreed(event: EntityBreedEvent) {
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