package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable

class DamageListener : Listener {

    private val damageCooldown = mutableMapOf<Player, Long>()
    private val DAMAGE_THRESHOLD = 8.0 // Health (1 heart = 2 hp)
    private val TIME_FRAME_MS = 5000L // Time (in milliseconds)

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        val attacker = event.damager as? Player ?: return

        // Check if the damage source is another player
        if (attacker != victim) {
            val currentTime = System.currentTimeMillis()

            // Update the last damage time for the victim
            damageCooldown[victim] = currentTime

            // Schedule a task to check if the victim took excessive damage within the time frame
            object : BukkitRunnable() {
                override fun run() {
                    val lastDamageTime = damageCooldown[victim] ?: return
                    if (currentTime - lastDamageTime <= TIME_FRAME_MS) {
                        // Calculate total damage taken within the time frame
                        val totalDamageTaken = event.damage + (damageCooldown.size - 1) * event.damage

                        // Check if the total damage exceeds the threshold
                        if (totalDamageTaken >= DAMAGE_THRESHOLD) {
                            Bukkit.broadcastMessage("${victim.name} is taking damage from other players!")
                            // TODO: ADD LOGIC FOR DETECTION!
                        }
                    }
                    damageCooldown.remove(victim)
                }
            }.runTaskLater(NationsEvent.INSTANCE, 20L) // 20 ticks = 1 second
        }
    }
}
