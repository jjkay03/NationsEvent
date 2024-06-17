package com.jjkay03.nationsevent.specific.ng3

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class SeasonSpecificNG3 : Listener {

    companion object {
        const val PERM_GROUP_COP: String = "group.cop"
        const val PERM_GROUP_ROBBER: String = "group.robber"
        const val PERM_GROUP_PRISONER: String = "group.prisoner"
    }


    // === VARIABLES ==================================================================================================

    private val catchPlayerHealth = 6.0 // 6.0 health points = 3 hearts
    private val minHealthAfterAttack = 2.0 // 2.0 health points = 1 heart


    // === EVENT HANDLES ==============================================================================================

    @EventHandler
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        // End if the attacker and damaged entity are not players
        if (!(event.entity is Player && event.damager is Player)) return

        val damagedPlayer = event.entity as Player
        val attackerPlayer = event.damager as Player
        val healthAfterDamage = damagedPlayer.health - event.finalDamage

        // TODO : Check if attackerPlayer is cop
        // TODO: Check if DamagedPlayer is robber

        // End if the damaged player's health is above the catchPlayerHealth threshold
        if (healthAfterDamage > catchPlayerHealth) return

        // Broadcast message if the damaged player's health is below the threshold
        val message = "${damagedPlayer.name} is under 3 hearts of health! Attacker: ${attackerPlayer.name}"
        Bukkit.broadcastMessage(message)

        // Prevent the player from dying if took too much damage
        if (healthAfterDamage <= 0) {
            Bukkit.broadcastMessage("§7Healed ${damagedPlayer.name} to 1 heart to avoid death")
            damagedPlayer.health = minHealthAfterAttack
            event.isCancelled = true // Cancel the original damage to avoid death
        }

        // Send robber to prison
        sendRobberToPrison(damagedPlayer)
    }


    // === FUNCTIONS ==================================================================================================

    // Function to send payer to robber to prison
    private fun sendRobberToPrison(player: Player) {
        // TODO : Run function to send DamagedPlayer to prison

        // DEBUG
        Bukkit.broadcastMessage("§aSENDING §f${player.name} §aTO PRISON")
    }


}
