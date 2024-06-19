package com.jjkay03.nationsevent.specific.ng3

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class NG3_SeasonSpecific : Listener {

    companion object {
        const val PERM_GROUP_COP: String = "group.cop"
        const val PERM_GROUP_ROBBER: String = "group.robber"
        const val PERM_GROUP_PRISONER: String = "group.prisoner"
    }


    // === VARIABLES ==================================================================================================

    private val prisonPrefix = "§8[§6Prison§8] §7"

    private val config = NationsEvent.INSTANCE.config
    private val prisonLocationConfig: Map<String, Any>? = config.getConfigurationSection("ng3-prison-location")?.getValues(false)

    private val catchPlayerHealth = 6.0 // 6.0 health points = 3 hearts
    private val minHealthAfterAttack = 2.0 // 2.0 health points = 1 heart


    // === EVENT HANDLES ==============================================================================================

    // Event Handler - Cop catch robber
    @EventHandler(ignoreCancelled = true)
    fun onPlayerAttackCopAttackRobber(event: EntityDamageByEntityEvent) {
        // End if the attacker and damaged entity are not players
        if (!(event.entity is Player && event.damager is Player)) return

        val damagedPlayer = event.entity as Player
        val attackerPlayer = event.damager as Player
        val healthAfterDamage = damagedPlayer.health - event.finalDamage

        // Check if correct players are performing the actions
        if (!attackerPlayer.hasPermission(PERM_GROUP_COP)) return  // End if attackerPlayer is not cop
        if (damagedPlayer.hasPermission(PERM_GROUP_PRISONER)) return // End if damagedPlayer already in prison
        if (!damagedPlayer.hasPermission(PERM_GROUP_ROBBER)) return // End if damagedPlayer is not robber

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

    // Event Handler - Robber free prisoner
    @EventHandler(ignoreCancelled = true)
    fun onPlayerAttackRobberAttackPrisoner(event: EntityDamageByEntityEvent) {
        // End if the attacker and damaged entity are not players
        if (!(event.entity is Player && event.damager is Player)) return

        val damagedPlayer = event.entity as Player
        val attackerPlayer = event.damager as Player

        // Check if correct players are performing the actions
        if (attackerPlayer.hasPermission(PERM_GROUP_PRISONER)) return // End if attackerPlayer is prisoner
        if (!attackerPlayer.hasPermission(PERM_GROUP_ROBBER)) return // End if attackerPlayer not robber
        if (!damagedPlayer.hasPermission(PERM_GROUP_PRISONER)) return // End of damagedPlayer not prisoner

        // Cancel the original damage to avoid death
        event.isCancelled = true

        // Send prisoner to robber (escape)
        sendPrisonToRobber(attackerPlayer, damagedPlayer)
    }


    // === FUNCTIONS ==================================================================================================

    // Teleport robber to prion
    private fun sendRobberToPrison(player: Player) {
        var prisonLocation: Location? = null

        // Notify staff and cops
        val notificationMessage = prisonPrefix + "§c[⛓CAUGHT]§7 Sending §f${player.name} §7to prison"
        Utils.messageStaff(notificationMessage)
        Utils.messagePlayerWithPerm(notificationMessage, PERM_GROUP_COP)

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

        // Give robber prisoner perms
        val consoleCommand = "lp user ${player.name} permission set $PERM_GROUP_PRISONER true"
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand)
    }

    // Teleport prisoner to robber (escape)
    private fun sendPrisonToRobber(playerRobber: Player, playerPrisoner: Player) {
        // Notify staff
        Utils.messageStaff(prisonPrefix + "§a[✋ESCAPE]§7 ${playerRobber.name} freed §f${playerPrisoner.name} §7from prison")

        // Teleport prisoner to robber
        playerPrisoner.teleport(playerRobber.location) // TP player
        playerPrisoner.world.playSound(playerPrisoner.location, Sound.ENTITY_PHANTOM_FLAP, 1.0f, 1.0f) // Play sound

        // Remove robber's prisoner perms
        val consoleCommand = "lp user ${playerPrisoner.name} permission unset $PERM_GROUP_PRISONER"
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand)
    }

}
