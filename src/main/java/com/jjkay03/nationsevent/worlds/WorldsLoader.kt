package com.jjkay03.nationsevent.worlds

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.WorldCreator

class WorldsLoader {

    // INITIALIZATION
    init {
        load(Config.WORLDS_LOADER_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        // End if feature disabled
        if (!enabled) return

        // Load all worlds
        loadAllWorlds()

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading world manager: ${this::class.simpleName}")
    }

    // Function to load all worlds from config
    private fun loadAllWorlds() {
        val worldsToLoad = Config.WORLDS_LOADER_WORLDS

        // End if list is empty
        if (worldsToLoad.isEmpty()) { return }

        // Load all worlds
        worldsToLoad.forEach { worldName -> loadWorld(worldName) }

    }

    // Function to load a single world
    private fun loadWorld(worldName: String) {
        // Use scheduler for world loading
        Scheduler.task(
            type = Scheduler.SchedulerType.GLOBAL,
            task = {
                // Get or create world
                val world = Bukkit.getWorld(worldName) ?: Bukkit.createWorld(WorldCreator.name(worldName))

                // Console feedback
                if (world != null) NationsEvent.INSTANCE.logger.info("${this::class.simpleName} - Loaded world: $worldName")
                else NationsEvent.INSTANCE.logger.severe("${this::class.simpleName} - Failed to load world: $worldName")
            }
        )
    }

}
