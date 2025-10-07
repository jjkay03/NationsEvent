package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.World

class WorldsTimeSync {

    private var task: Any? = null
    private var worldsToSync: List<World> = emptyList()

    // INITIALIZATION
    init {
        load(Config.FEATURES_WORLDS_TIME_SYNC_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        // End if feature disabled
        if (!enabled) return

        // Start time sync task
        task = Scheduler.taskRepeating(
            type = Scheduler.SchedulerType.GLOBAL,
            delayTicks = 200L, // 10 seconds initial delay
            periodTicks = 200L, // 10 seconds repeat
            task = { syncWorldTimes() }
        )

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading feature: ${this::class.simpleName}")
    }

    // Helper function to get valid worlds from config list
    private fun getValidWorldsFromConfig(): List<World> {
        val configWorlds = Config.FEATURES_WORLDS_TIME_SYNC_WORLDS
        val validWorlds = mutableListOf<World>()
        configWorlds.forEach { worldName ->
            val world = Bukkit.getWorld(worldName)
            if (world != null) { validWorlds.add(world) }
            else { NationsEvent.INSTANCE.logger.warning("${this::class.simpleName} - World '$worldName' not found!") }
        }
        return validWorlds
    }

    // Helper function to sync all worlds to the first world's time
    private fun syncWorldTimes() {
        // End if list is empty
        if (worldsToSync.isEmpty()) return

        // Generate list of worlds
        worldsToSync = getValidWorldsFromConfig()

        // Set time in all words
        val masterWorld = worldsToSync.first()
        val masterTime = masterWorld.time
        worldsToSync.drop(1).forEach { world ->
            if (world.time != masterTime) {
                world.time = masterTime
            }
        }
    }
}
