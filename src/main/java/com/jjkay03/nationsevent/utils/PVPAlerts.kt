package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.commands.PVPAlertsCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class PVPAlerts : Listener {

    private val damageCooldown = mutableMapOf<Player, MutableMap<Player, Double>>()
    private val damageThreshold = 8.0 // Health (1 heart = 2 hp)
    private val timeFrameMS = 2000L // Time (in milliseconds)

    // Event handler that tracks player getting damaged
    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        val attacker = event.damager as? Player ?: return

        if (attacker == victim) { return } // End if attacker is the victim
        updateDamageRecord(victim, attacker, event.damage) // Update damage tracking list

        // Callback function to send alert when excessive damage is detected
        scheduleExcessiveDamageCheck(victim) { sendAlert(victim) }
    }

    // Function that updates how much a player is getting damaged
    private fun updateDamageRecord(victim: Player, attacker: Player, damage: Double) {
        damageCooldown.getOrPut(victim) { mutableMapOf() }
            .merge(attacker, damage) { oldDamage, newDamage -> oldDamage + newDamage }
    }

    // Function that schedules a task to check for excessive damage and returns true if excessive damage is detected
    private fun scheduleExcessiveDamageCheck(victim: Player, callback: () -> Unit) {
        object : BukkitRunnable() {
            override fun run() {
                val damageMap = damageCooldown[victim] ?: return
                val totalDamageTaken = damageMap.values.sum()
                if (totalDamageTaken >= damageThreshold) {
                    callback() // Execute the callback when excessive damage is detected
                }
                damageCooldown.remove(victim)
            }
        }.runTaskLater(NationsEvent.INSTANCE, timeFrameMS / 50) // Convert ms to ticks
    }

    // Function that alerts correct players
    private fun sendAlert(victim: Player) {
        // DEBUG
        Bukkit.getLogger().info("[PVPALERT] ${victim.name} is taking excessive damage from other players!")

        val playersToAlert: List<UUID> = PVPAlertsCommand.PVP_ALERTS_PLAYERS.map { UUID.fromString(it) }

        // Check if a player is in the list to alert and has the required permission
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (playersToAlert.any { it == player.uniqueId } && player.hasPermission(NationsEvent.PERM_STAFF)) {
                player.sendMessage("§b${victim.name} is taking excessive damage from other players!")
            }
        }

    }


}
