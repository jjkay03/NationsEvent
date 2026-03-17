package com.jjkay03.nationsevent.specific.island_cold

import com.jjkay03.nationsevent.specific.EventSpecific
import com.jjkay03.nationsevent.utils.Scheduler
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player

class IslandColdFrostbite {

    // INITIALIZATION
    init {
        startFrostbiteTask()
    }

    // Check players exposed to rain/storm periodically
    private fun startFrostbiteTask() {
        Scheduler.taskRepeating(Scheduler.SchedulerType.GLOBAL, 1L, 1L, {
            val coldWorld = EventSpecific.WORLD_NS8_COLD ?: return@taskRepeating

            // End if not rain/storm
            if (coldWorld.isClearWeather) return@taskRepeating

            for (player in coldWorld.players) {
                // End if player has bypass or in spectator mode
                if (player.hasPermission(EventSpecific.PERM_NS8_BYPASS_FROSTBITE) ||
                    player.gameMode == GameMode.SPECTATOR) continue

                // Skip if player is wearing full armor
                if (hasFullArmor(player)) continue

                // Freeze
                val newTicks = (player.freezeTicks + 3).coerceAtMost(300)
                player.freezeTicks = newTicks

                // Notify
                if (newTicks > 100) player.sendActionBar(Component.text("§b❄ Frostbite"))
            }
        })
    }

    // Check if player has full armor equipped
    private fun hasFullArmor(player: Player): Boolean {
        val inventory = player.inventory
        val helmet = inventory.helmet
        val chestplate = inventory.chestplate
        val leggings = inventory.leggings
        val boots = inventory.boots

        return helmet != null && helmet.type != Material.AIR &&
                chestplate != null && chestplate.type != Material.AIR &&
                leggings != null && leggings.type != Material.AIR &&
                boots != null && boots.type != Material.AIR
    }
}