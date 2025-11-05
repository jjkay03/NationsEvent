package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent

class DisableWolfBreeding : Listener {

    // List of entities to disable breeding for
    val entityList = listOf<EntityType>(
        EntityType.WOLF,
        EntityType.CAT
    )

    // INITIALIZATION
    init {
        load(Config.SETTINGS_DISABLE_WOLF_BREEDING)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    @EventHandler
    fun onEntityBreed(event: EntityBreedEvent) {
        // If the entity being bred is a wolf, cancel breeding
        if (event.entity.type in entityList) {
            // Cancel event
            event.isCancelled = true

            // If a player caused the breeding, notify them
            val breeder = event.breeder
            if (breeder is Player) {
                breeder.sendMessage("§c${event.entity.type.name} breeding is disabled!")
            }
        }
    }
}