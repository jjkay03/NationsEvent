package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable

class PVPAlerts : Listener {

    private val damageCooldown = mutableMapOf<Player, MutableMap<Player, Double>>()
    private val DAMAGE_THRESHOLD = 8.0 // Health (1 heart = 2 hp)
    private val TIME_FRAME_MS = 5000L // Time (in milliseconds)

    // Event handler that tracks player getting damaged
    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        val attacker = event.damager as? Player ?: return

        if (attacker == victim) { return } // End if attacker is the victim
        updateDamageRecord(victim, attacker, event.damage) // Update damage tracking list
        if (!scheduleExcessiveDamageCheck(victim)) { return } // End if victim did not take excessive damage

        // Victim took excessive damage
        Bukkit.broadcastMessage("${victim.name} is taking excessive damage from other players!")
        // TODO: ADD LOGIC FOR DETECTION!
    }

    // Function that updates how much a player is getting damaged
    private fun updateDamageRecord(victim: Player, attacker: Player, damage: Double) {
        damageCooldown.getOrPut(victim) { mutableMapOf() }
            .merge(attacker, damage) { oldDamage, newDamage -> oldDamage + newDamage }
    }

    // Function that schedules a task to check for excessive damage and returns true if excessive damage is detected
    private fun scheduleExcessiveDamageCheck(victim: Player): Boolean {
        var excessiveDamageDetected = false
        object : BukkitRunnable() {
            override fun run() {
                val damageMap = damageCooldown[victim] ?: return
                val totalDamageTaken = damageMap.values.sum()
                if (totalDamageTaken >= DAMAGE_THRESHOLD) { excessiveDamageDetected = true }
                damageCooldown.remove(victim)
            }
        }.runTaskLater(NationsEvent.INSTANCE, TIME_FRAME_MS / 50) // Convert ms to ticks

        return excessiveDamageDetected
    }
}
