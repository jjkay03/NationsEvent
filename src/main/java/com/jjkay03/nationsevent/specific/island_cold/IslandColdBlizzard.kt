package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.Scheduler
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

class IslandColdBlizzard : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
    }

    // Reduce food restoration in cold weather
    @EventHandler
    fun onFoodConsume(event: PlayerItemConsumeEvent) {
        // End if not on cold island
        val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return
        if (coldWorld != event.player.world) return

        // End if not thunder weather
        if (!coldWorld.isThundering) return

        // Get player and store current food level and saturation before consumption
        val player = event.player
        val foodBefore = player.foodLevel
        val saturationBefore = player.saturation

        // Schedule food level adjustment after the event
        Scheduler.taskDelayed(
            type = Scheduler.SchedulerType.ENTITY,
            delayTicks = 1L,
            task = {
                // Calculate how much food was gained
                val foodAfter = player.foodLevel
                val foodGained = foodAfter - foodBefore

                // If food was gained, reduce to 5%
                if (foodGained > 0) {
                    val reducedGain = (foodGained * 0.05).toInt().coerceAtLeast(1)
                    player.foodLevel = (foodBefore + reducedGain).coerceIn(0, 20)

                    // Send action bar message
                    player.sendActionBar(Component.text("§b❄ Food is insufficient"))
                }

                // Remove all saturation gained from eating
                player.saturation = saturationBefore
            },
            entity = player
        )
    }

}