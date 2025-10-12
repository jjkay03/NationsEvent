package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.concurrent.ConcurrentHashMap

class TeleportBackCommand(private val commandName: String) : CommandExecutor, TabCompleter, Listener {

    companion object {
        val LAST_TELEPORT_LOCATION = ConcurrentHashMap<Player, Location>()
    }

    // INITIALIZATION (Register command and events)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if console and no player is specified
        if (sender !is Player && args.isEmpty()) { sender.sendMessage("§cConsole must specify a player!"); return true }

        // Get target player
        val targetPlayer: Player = if (args.isNotEmpty()) {
            Bukkit.getPlayer(args[0]) ?: run { sender.sendMessage("§cPlayer not found!"); return true }
        } else {
            sender as Player
        }

        // Get last location
        val lastLocation = LAST_TELEPORT_LOCATION[targetPlayer]
        if (lastLocation == null) {
            val message =
                if (targetPlayer == sender) "§cYou don't have a previous location to teleport back to!"
                else "§c${targetPlayer.name} doesn't have a previous location to teleport back to!"
            sender.sendMessage(message)
            return true
        }

        // Teleport player
        targetPlayer.teleportAsync(lastLocation)

        // Send feedback
        val successMessage =
            if (targetPlayer == sender) "§aTeleported back to your previous location"
            else "§aTeleported ${targetPlayer.name} back to their previous location"
        sender.sendMessage(successMessage)
        if (targetPlayer != sender) targetPlayer.sendMessage("§eTeleported back to your previous location by ${sender.name}")

        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }

    // EVENT LISTENER - Save location before teleport
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.cause == PlayerTeleportEvent.TeleportCause.COMMAND ||
            event.cause == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            LAST_TELEPORT_LOCATION[event.player] = event.from.clone()
        }
    }

}
