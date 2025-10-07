package com.jjkay03.nationsevent.features

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Config
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World

class WorldsSync {

    private var task: Any? = null
    private var worldsToSync: List<World> = emptyList()

    // INITIALIZATION
    init {
        load(Config.FEATURES_WORLDS_SYNC_ENABLE)
    }

    // LOAD (If enabled in config)
    fun load(enabled: Boolean) {
        // End if feature disabled
        if (!enabled) return

        // Start sync task
        task = Scheduler.taskRepeating(
            type = Scheduler.SchedulerType.GLOBAL,
            delayTicks = 200L, // 10 seconds initial delay
            periodTicks = 200L, // 10 seconds repeat
            task = { syncWorlds() }
        )

        // Console message
        NationsEvent.INSTANCE.logger.info("- Loading feature: ${this::class.simpleName}")
    }

    // Function to sync all worlds
    private fun syncWorlds() {
        // Get worlds list
        worldsToSync = getValidWorldsFromConfig()

        // End if list is empty
        if (worldsToSync.isEmpty()) return

        // Get master world
        val masterWorld = worldsToSync.first()

        // Go through all the worlds
        worldsToSync.drop(1).forEach { world ->
            // Sync time
            if (Config.FEATURES_WORLDS_SYNC_ENABLE_TIME) {
                if (world.time != masterWorld.time) { world.time = masterWorld.time }
            }

            // Sync weather
            if (Config.FEATURES_WORLDS_SYNC_ENABLE_WEATHER) {
                syncWeather(masterWorld, world)
            }

            // Sync game rules
            if (Config.FEATURES_WORLDS_SYNC_ENABLE_GAME_RULES) {
                syncGameRules(masterWorld, world)
            }

            // Sync difficulty
            if (Config.FEATURES_WORLDS_SYNC_ENABLE_DIFFICULTY) {
                if (world.difficulty != masterWorld.difficulty) { world.difficulty = masterWorld.difficulty }
            }
        }
    }

    // Helper function to get valid worlds from config list
    private fun getValidWorldsFromConfig(): List<World> {
        val configWorlds = Config.FEATURES_WORLDS_SYNC_WORLDS
        val validWorlds = mutableListOf<World>()
        configWorlds.forEach { worldName ->
            val world = Bukkit.getWorld(worldName)
            if (world != null) { validWorlds.add(world) }
            else { NationsEvent.INSTANCE.logger.warning("${this::class.simpleName} - World '$worldName' not found!") }
        }
        return validWorlds
    }

    // Helper function to sync weather from master to target world
    private fun syncWeather(masterWorld: World, targetWorld: World) {
        // Sync storm state
        if (targetWorld.hasStorm() != masterWorld.hasStorm()) {
            targetWorld.setStorm(masterWorld.hasStorm())
        }

        // Sync thunder state
        if (targetWorld.isThundering != masterWorld.isThundering) {
            targetWorld.isThundering = masterWorld.isThundering
        }

        // Sync weather durations
        if (targetWorld.weatherDuration != masterWorld.weatherDuration) {
            targetWorld.weatherDuration = masterWorld.weatherDuration
        }

        // Sync thunder duration
        if (targetWorld.thunderDuration != masterWorld.thunderDuration) {
            targetWorld.thunderDuration = masterWorld.thunderDuration
        }
    }

    // Helper function to sync game rules from master to target world
    private fun syncGameRules(masterWorld: World, targetWorld: World) {
        GameRule.values().forEach { gameRule ->
            // Skip experimental features if disabled
            if (gameRule.requiredFeatures().any { !masterWorld.featureFlags.contains(it) }) return@forEach

            // Get values
            val masterValue = masterWorld.getGameRuleValue(gameRule)
            val targetValue = targetWorld.getGameRuleValue(gameRule)

            // If different update game rule
            if (masterValue != targetValue) {
                when (masterValue) {
                    is Boolean -> targetWorld.setGameRule(gameRule as GameRule<Boolean>, masterValue)
                    is Int -> targetWorld.setGameRule(gameRule as GameRule<Int>, masterValue)
                    is String -> targetWorld.setGameRule(gameRule as GameRule<String>, masterValue)
                    else -> NationsEvent.INSTANCE.logger.severe("${this::class.simpleName} - Invalid game rule '${gameRule.name}' type ${gameRule.type}!")
                }
            }
        }
    }
}
