package com.jjkay03.nationsevent.commands.management

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PVPToggleCommand(private val commandName: String) : CommandExecutor, TabCompleter, Listener {

    companion object { var PVP = false }

    // INITIALIZATION (Register command and events)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        var silent = false

        // Toggle PVP state
        PVP = !PVP

        // Check if the first argument is "silent"
        if (args.isNotEmpty() && args[0].lowercase() in setOf("silent", "s")) { silent = true }

        // Notify all players on the server if not silent
        if (!silent) {
            Bukkit.getServer().onlinePlayers.forEach { player ->
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
                player.sendMessage(
                    if (PVP) "§a\uD83D\uDDE1 PVP has been ENABLED!"
                    else "§c\uD83D\uDDE1 PVP has been DISABLED!"
                )
            }
        } else {
            sender.sendMessage(
                if (PVP) "§7\uD83D\uDDE1 PVP has been §aENABLED §7silently"
                else "§7\uD83D\uDDE1 PVP has been §cDISABLED §7silently"
            )
        }

        return true
    }

    // TAB COMPLETE
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("silent")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }

    // Event handler to handle hits
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (PVP) return // End if PVP is off
        if (event.damager is Player && event.entity is Player) event.isCancelled = true
    }

    // Event handler to handle arrows
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (PVP) return // End if PVP is off
        val damaged = event.entity as? Player ?: return       // End if damaged entity isn't player
        val projectile = event.damager as? Arrow ?: return    // End if damage not caused by arrow
        val shooter = projectile.shooter as? Player ?: return // End if arrow not shot by player
        event.isCancelled = true
    }
}