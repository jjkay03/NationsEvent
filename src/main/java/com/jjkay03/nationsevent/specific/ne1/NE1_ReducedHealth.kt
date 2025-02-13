package com.jjkay03.nationsevent.specific.ne1

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class NE1_ReducedHealth : Listener, CommandExecutor {

    companion object {
        var REDUCED_HEALTH_STATE = false
    }

    private val bypassReducedHealthPermission = "nationsevent.ne1.bypassreducedhealth"
    private val maxHealthVanilla : Double = 20.0
    private val maxHealthNE1 : Double = 16.0

    // Deal with setting health when players join
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.hasPermission(bypassReducedHealthPermission)) return
        if (REDUCED_HEALTH_STATE) setPlayerMaxHealth(event.player, maxHealthNE1, false)
        else setPlayerMaxHealth(event.player, maxHealthVanilla, false)
    }

    // Command - /togglereducehealth
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Switch state of reducedHeath
        REDUCED_HEALTH_STATE = !REDUCED_HEALTH_STATE

        // If reduced health got enabled
        if (REDUCED_HEALTH_STATE) {
            sender.sendMessage("§7❤ Reduced health has been §aENABLED §8(Max health: $maxHealthNE1)")
            changeAllPlayersMaxHealth(maxHealthNE1)
        }

        // If reduced health got disabled
        else {
            sender.sendMessage("§7❤ Reduced health has been §cDISABLED §8(Max health: $maxHealthVanilla)")
            changeAllPlayersMaxHealth(maxHealthVanilla)
        }

        return true
    }

    // Function that changes the max health of a player
    private fun setPlayerMaxHealth(player: Player, maxHealth: Double, notifyPlayer: Boolean = true) {
        val healthAttribute = player.getAttribute(Attribute.MAX_HEALTH)
        healthAttribute?.baseValue = maxHealth
        if (player.health > maxHealth) player.health = maxHealth
        if (notifyPlayer) player.sendMessage("§7❤ Your max health is now $maxHealth HP")
    }

    // Function that changes max health to all players
    private fun changeAllPlayersMaxHealth(maxHealth: Double) {
        Bukkit.getOnlinePlayers()
            .filter { !it.hasPermission(bypassReducedHealthPermission) }
            .forEach { setPlayerMaxHealth(it, maxHealth) }
    }

}