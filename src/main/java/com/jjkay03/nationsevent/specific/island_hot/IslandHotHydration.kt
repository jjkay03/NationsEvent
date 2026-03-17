package com.jjkay03.nationsevent.specific.island_hot

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.Scheduler
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import java.util.*

class IslandHotHydration : Listener {

    companion object {
        private const val DEHYDRATION_MAX = 1200
        private const val DEHYDRATION_ALERT = DEHYDRATION_MAX - 30 // 30s before max
        private const val WATER_BOTTLE_HYDRATION = 500
    }

    private val dehydrationMap = mutableMapOf<UUID, Int>()

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
        startDehydrationTask()
    }

    // Check players and increase dehydration periodically
    private fun startDehydrationTask() {
        Scheduler.taskRepeating(Scheduler.SchedulerType.GLOBAL, 1L, 20L, {
            val hotWorld = EventSpecific.WORLD_NS8_HOT ?: return@taskRepeating

            for (player in hotWorld.players) {
                // Skip if player has bypass or in spectator mode
                if (player.hasPermission(EventSpecific.PERM_NS8_BYPASS_HYDRATION) ||
                    player.gameMode == GameMode.CREATIVE ||
                    player.gameMode == GameMode.SPECTATOR) continue

                // Increase dehydration
                val currentDehydration = dehydrationMap.getOrDefault(player.uniqueId, 0)
                val newDehydration = (currentDehydration + 1).coerceAtMost(DEHYDRATION_MAX)
                dehydrationMap[player.uniqueId] = newDehydration

                // Skip if not at alert level yet
                if (newDehydration < DEHYDRATION_ALERT) continue

                // Show dehydration warning (different message based on severity)
                val message = if (newDehydration >= DEHYDRATION_MAX) "§c⚠ Hydrate" else "§c\uD83E\uDDEA Hydrate"
                player.sendActionBar(Component.text(message))

                // Skip draining if not at max dehydration
                if (newDehydration < DEHYDRATION_MAX) continue

                // Drain saturation first (always)
                if (player.saturation > 0) {
                    player.saturation = (player.saturation - 1f).coerceAtLeast(0f)
                    continue
                }

                // Drain hunger with 50/50 chance
                if (Math.random() >= 0.5) continue
                if (player.foodLevel <= 0) continue
                player.foodLevel = (player.foodLevel - 1).coerceAtLeast(0)
            }
        })
    }

    // Event handler for consuming water bottles
    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        if (event.item.type != Material.POTION) return

        val player = event.player

        // If player is in the dehydration map, reduce their dehydration
        if (dehydrationMap.containsKey(player.uniqueId)) {
            val currentDehydration = dehydrationMap[player.uniqueId]!!
            val newDehydration = (currentDehydration - WATER_BOTTLE_HYDRATION).coerceAtLeast(0)
            dehydrationMap[player.uniqueId] = newDehydration
        }

        // Notify
        player.sendActionBar(Component.text("§b\uD83E\uDDEA Hydrated"))
    }
}
