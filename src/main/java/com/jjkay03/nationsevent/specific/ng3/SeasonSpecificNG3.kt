package com.jjkay03.nationsevent.specific.ng3

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils
import org.bukkit.Location
import org.bukkit.Sound
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

    private val config = NationsEvent.INSTANCE.config
    private val prisonLocationConfig: Map<String, Any>? = config.getConfigurationSection("ng3-prison-location")?.getValues(false)

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

        // Prevent the player from dying if took too much damage
        if (healthAfterDamage <= 0) {
            damagedPlayer.health = minHealthAfterAttack
            event.isCancelled = true // Cancel the original damage to avoid death
        }

        // Send robber to prison
        sendRobberToPrison(damagedPlayer)
    }


    // === FUNCTIONS ==================================================================================================

    // Teleport player to prion
    private fun sendRobberToPrison(player: Player) {
        var prisonLocation: Location? = null

        // Notify staff
        Utils.messageStaff("§7⛓ Sending ${player.name} to prison")

        // Get location from config
        if (prisonLocationConfig != null) {
            val x = prisonLocationConfig["x"] as? Double ?: 0.0
            val y = prisonLocationConfig["y"] as? Double ?: 100.0
            val z = prisonLocationConfig["z"] as? Double ?: 0.0
            prisonLocation = Location(player.world, x, y, z)
        } else {
            NationsEvent.INSTANCE.logger.warning("Prison location is not set in the config!")
        }

        // TP player to prison
        prisonLocation?.let {
            player.teleport(it) // TP player
            player.world.playSound(it, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f) // Play sound
        }
    }


}
