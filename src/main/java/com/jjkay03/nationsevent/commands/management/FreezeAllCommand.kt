package com.jjkay03.nationsevent.commands.management

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent

class FreezeAllCommand(private val commandName: String) : CommandExecutor, Listener {

    companion object { var ENABLED = false }
    private val frozenMessage = "§cAll players are frozen"

    // INITIALIZATION (Register command and events)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // Flip ENABLED bool
        ENABLED = !ENABLED

        // Notify all players on the server
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            player.sendMessage(
                if (ENABLED) "§c❄ All players have been FROZEN!"
                else "§a❄ All players are UNFROZEN!"
            )
        }

        return true
    }

    // Event to handle player movement
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // End if freeze is off or player has bypass perm
        if (!ENABLED || event.player.hasPermission(Saves.PERM_FREEZE_ALL_BYPASS)) return

        // Check if player is riding a vehicle and eject them
        if (event.player.isInsideVehicle) {
            event.player.vehicle?.removePassenger(event.player)
            event.player.sendMessage("$frozenMessage - You can't ride vehicles!")
            return
        }

        val from = event.from
        val to = event.to
        if (to.y < from.y) return // Allow the player to fall
        // Prevent all other movement (X and Z axes)
        if (from.x != to.x || from.z != to.z || from.y != to.y) {
            event.to = from.setDirection(to.direction) // Preserve yaw/pitch, but reset position
            event.player.sendMessage("$frozenMessage - You can't move!")
        }
    }

    // Event to handle block breaking
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        // End if freeze is off or player has bypass perm
        if (!ENABLED || event.player.hasPermission(Saves.PERM_FREEZE_ALL_BYPASS)) return
        // Cancel block break
        event.isCancelled = true
        event.player.sendMessage("$frozenMessage - You can't break!")
    }

    // Event to handle block placing
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        // End if freeze is off or player has bypass perm
        if (!ENABLED || event.player.hasPermission(Saves.PERM_FREEZE_ALL_BYPASS)) return
        // Cancel block place
        event.isCancelled = true
        event.player.sendMessage("$frozenMessage - You can't place!")
    }

    // Event to handle entity damage
    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (!ENABLED || event.entity !is Player) return
        val player = event.entity as Player
        if (player.hasPermission(Saves.PERM_FREEZE_ALL_BYPASS)) return
        // Cancel the damage event
        event.isCancelled = true
        player.sendMessage("$frozenMessage - You are protected from damage!")
    }
}
