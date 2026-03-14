package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.GameMode
import org.bukkit.event.Listener

class IslandColdWater : Listener {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
        startFreezeTask()
    }

    // Check players in water periodically
    private fun startFreezeTask() {
        Scheduler.taskRepeating(Scheduler.SchedulerType.GLOBAL, 1L, 1L, {
            val world = EventSpecific.WORLD_NS8_COLD ?: return@taskRepeating
            for (player in world.players) {
                if (player.gameMode == GameMode.SPECTATOR) continue
                if (!player.isInWater) continue
                val newTicks = (player.freezeTicks + 3).coerceAtMost(300)
                player.freezeTicks = newTicks
            }
        })
    }
}

