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

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        val attacker = event.damager as? Player ?: return

        // Check if the damage source is another player
        if (attacker != victim) {
            updateDamageRecord(victim, attacker, event.damage)
            scheduleExcessiveDamageCheck(victim)
        }
    }

    private fun updateDamageRecord(victim: Player, attacker: Player, damage: Double) {
        damageCooldown.getOrPut(victim) { mutableMapOf() }
            .merge(attacker, damage) { oldDamage, newDamage -> oldDamage + newDamage }
    }

    private fun scheduleExcessiveDamageCheck(victim: Player) {
        val currentTime = System.currentTimeMillis()

        object : BukkitRunnable() {
            override fun run() {
                val damageMap = damageCooldown[victim] ?: return
                val totalDamageTaken = damageMap.values.sum()
                if (totalDamageTaken >= DAMAGE_THRESHOLD) {
                    Bukkit.broadcastMessage("${victim.name} is taking excessive damage from other players!")
                    // TODO: ADD LOGIC FOR DETECTION!
                }
                damageCooldown.remove(victim)
            }
        }.runTaskLater(NationsEvent.INSTANCE, TIME_FRAME_MS / 50) // Convert ms to ticks
    }
}
