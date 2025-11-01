package com.jjkay03.nationsevent.settings

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import org.bukkit.Bukkit
import org.bukkit.entity.Monster
import org.bukkit.entity.Raider
import org.bukkit.entity.Slime
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class DisableHostileMobSpawn: Listener {

    // INITIALIZATION
    init {
        load(Config.SETTINGS_DISABLE_HOSTILE_MOBS_SPAWN)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        if (!enabled) return
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
        NationsEvent.INSTANCE.logger.info("- Loading setting: ${this::class.simpleName}")
    }

    // Handle hostile mob spawning
    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        // End if spawn reason isn't natural (spawn egg, command)
        if (event.spawnReason != CreatureSpawnEvent.SpawnReason.NATURAL) return

        // Check if entity is a hostile
        if (
            event.entity is Monster ||
            event.entity is Slime ||
            event.entity is Raider
            ) {
            // Cancel the spawn
            event.isCancelled = true
        }

    }
}
