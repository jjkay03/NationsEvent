package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.Scheduler
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import java.util.UUID
import kotlin.random.Random

class IslandColdBlizzard : Listener {

    companion object {
        private const val FREEZE_INTERVAL_SECONDS = 120 // Seconds between freezes
        private const val FREEZE_MIN_DURATION_SECONDS = 10 // Min freeze duration
        private const val FREEZE_MAX_DURATION_SECONDS = 20 // Max freeze duration
    }

    private val lastFreezeTime = mutableMapOf<UUID, Long>()
    private val activeFreezeEnd = mutableMapOf<UUID, Long>()

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.server.pluginManager.registerEvents(this, NationsEvent.INSTANCE)
        startPeriodicFreezeTask()
    }

    // Periodically freeze players during thunderstorms
    private fun startPeriodicFreezeTask() {
        Scheduler.taskRepeating(Scheduler.SchedulerType.GLOBAL, 1L, 1L, {
            val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return@taskRepeating

            // End if not thundering
            if (!coldWorld.isThundering) return@taskRepeating

            val currentTime = System.currentTimeMillis()

            for (player in coldWorld.players) {
                // End if bypass permission
                if (player.hasPermission(EventSpecific.PERM_NS8_BYPASS_BLIZZARD)) continue

                // Skip if player in spectator mode
                if (player.gameMode == GameMode.SPECTATOR) continue

                val playerId = player.uniqueId

                // Check if player is currently being frozen
                val freezeEndTime = activeFreezeEnd[playerId]
                if (freezeEndTime != null) {
                    // Still in freeze period
                    if (currentTime < freezeEndTime) {
                        // Freeze the player
                        val newTicks = (player.freezeTicks + 3).coerceAtMost(300)
                        player.freezeTicks = newTicks

                        // Show action bar
                        if (newTicks > 100) player.sendActionBar(Component.text("§b❄ Blizzard"))
                    } else {
                        // Freeze period ended
                        activeFreezeEnd.remove(playerId)
                    }
                    continue
                }

                // Check if player is being tracked, if not initialize them
                if (!lastFreezeTime.containsKey(playerId)) {
                    lastFreezeTime[playerId] = currentTime
                    continue
                }

                // Check if it's time to start a new freeze period
                val lastFreeze = lastFreezeTime[playerId]!!
                val timeSinceLastFreeze = currentTime - lastFreeze

                if (timeSinceLastFreeze >= FREEZE_INTERVAL_SECONDS * 1000) {
                    // Start new freeze period
                    val freezeDuration = Random.nextInt(
                        FREEZE_MIN_DURATION_SECONDS,
                        FREEZE_MAX_DURATION_SECONDS + 1
                    ) * 1000L

                    lastFreezeTime[playerId] = currentTime
                    activeFreezeEnd[playerId] = currentTime + freezeDuration
                }
            }
        })
    }

    // Reduce food restoration in cold weather
    @EventHandler
    fun onFoodConsume(event: PlayerItemConsumeEvent) {
        // End if bypass permission
        val player = event.player
        if (player.hasPermission(EventSpecific.PERM_NS8_BYPASS_BLIZZARD)) return

        // End if not on cold island
        val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return
        if (coldWorld != event.player.world) return

        // End if not thunder weather
        if (!coldWorld.isThundering) return

        // Store current food level and saturation before consumption
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

    // 5% chance blocks turn into packed ice when placed during thunderstorms
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        // End if bypass permission
        val player = event.player
        if (player.hasPermission(EventSpecific.PERM_NS8_BYPASS_BLIZZARD)) return

        // End if not cold world
        val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return
        if (coldWorld != event.block.world) return

        // End if not thundering
        if (!coldWorld.isThundering) return

        // 5% chance (1 in 20)
        if (Random.nextInt(100) >= 5) return

        // Turn placed block into packed ice
        event.block.type = Material.PACKED_ICE
    }

}