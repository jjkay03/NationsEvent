package com.jjkay03.nationsevent.specific.ng5.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityResurrectEvent
import java.util.*

class NG5_ClericImmunity : CommandExecutor, Listener {

    private var protectedPlayer: UUID? = null
    private var protectionActive = false

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // Check if the command is being used correctly
        if (args.size != 1) {
            sender.sendMessage("§eReset cleric protected player back to null")
            protectedPlayer = null
            protectionActive = false
            return true
        }

        // Get the player by their name
        val targetPlayer: Player? = Bukkit.getPlayer(args[0])

        // Check if the player is online
        if (targetPlayer == null) {
            sender.sendMessage("§cPlayer ${args[0]} is not online!")
            return true
        }

        protectedPlayer = targetPlayer.uniqueId
        protectionActive = false
        sender.sendMessage("§a${targetPlayer.name} has been protected by cleric")

        return true
    }

    @EventHandler
    fun protectedPlayerDeath(event: EntityResurrectEvent) {
        val player = event.entity
        if (player.uniqueId != protectedPlayer) return // End if event player is not protected player
        (player as Player).sendTitle("", "§6§l✨ SAVED BY A MAGICAL BARRIER ✨", 5, 40, 5)
        event.isCancelled = false
        protectionActive = true
    }

    @EventHandler
    fun preventProtectedPlayerAttack(event: EntityDamageByEntityEvent) {
        // End if protection not active yet
        if (!protectionActive) return
        if (event.entity.uniqueId == protectedPlayer) {
            event.damager.sendMessage("§eThis player is protected by a magical barrier!")
            event.isCancelled = true
        }
        else if (event.damager.uniqueId == protectedPlayer) {
            event.damager.sendMessage("§cYou can't attack while being protected by a magical barrier!")
            event.isCancelled = true
        }
    }


}