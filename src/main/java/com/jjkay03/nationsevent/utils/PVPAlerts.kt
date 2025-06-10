package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import com.jjkay03.nationsevent.commands.others.PVPAlertsCommand
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class PVPAlerts : Listener {

    private val damageCooldown = mutableMapOf<Player, MutableMap<Player, Double>>()
    private val alertCooldown = mutableMapOf<Player, Long>()
    private val damagePercentageThreshold = 0.4 // 40% of the player's max health (equivalent to 4 hearts)
    private val timeFrameMS = 3000L // Time (in milliseconds)
    private val alertCooldownMS = 30000L // Cooldown time for alerts (in milliseconds)

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        val attacker = event.damager as? Player ?: return

        if (attacker == victim) { return } // End if attacker is the victim
        updateDamageRecord(victim, attacker, event.damage) // Update damage tracking list

        // Callback function to send alert when excessive damage is detected
        scheduleExcessiveDamageCheck(victim) { sendAlert(victim) }
    }

    private fun updateDamageRecord(victim: Player, attacker: Player, damage: Double) {
        damageCooldown.getOrPut(victim) { mutableMapOf() }
            .merge(attacker, damage) { oldDamage, newDamage -> oldDamage + newDamage }
    }

    private fun scheduleExcessiveDamageCheck(victim: Player, callback: () -> Unit) {
        object : BukkitRunnable() {
            override fun run() {
                val damageMap = damageCooldown[victim] ?: return
                val totalDamageTaken = damageMap.values.sum()
                val maxHealth = victim.maxHealth
                val threshold = maxHealth * damagePercentageThreshold

                if (totalDamageTaken >= threshold) {
                    val lastAlertTime = alertCooldown[victim]
                    val currentTime = System.currentTimeMillis()

                    if (lastAlertTime == null || currentTime - lastAlertTime >= alertCooldownMS) {
                        callback() // Execute the callback when excessive damage is detected
                        alertCooldown[victim] = currentTime
                    }
                }
                damageCooldown.remove(victim)
            }
        }.runTaskLater(NationsEvent.INSTANCE, timeFrameMS / 50) // Convert ms to ticks
    }

    private fun sendAlert(victim: Player) {
        val playersToAlert: List<UUID> = PVPAlertsCommand.PVP_ALERTS_PLAYERS.map { UUID.fromString(it) }

        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (playersToAlert.any { it == player.uniqueId } && player.hasPermission(Saves.PERM_STAFF)) {
                val message = TextComponent("§6\uD83D\uDDE1 PVP ALERT§6: §e${victim.name} §6is being attacked!")
                message.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("§eTeleport to ${victim.name}")))
                message.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${victim.name}")
                player.spigot().sendMessage(message)
            }
        }
    }
}
