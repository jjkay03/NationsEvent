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
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.ConcurrentHashMap

class TeleportOfflineCommand(private val commandName: String) : CommandExecutor, TabCompleter, Listener {

    companion object {
        val LOGOUT_PLAYER_LOCATIONS = ConcurrentHashMap<String, Location>()
    }

    // INITIALIZATION (Register command and events)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
        Bukkit.getPluginManager().registerEvents(this, NationsEvent.INSTANCE)
    }

    // LISTENER - Save location on logout
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        LOGOUT_PLAYER_LOCATIONS[event.player.name] = event.player.location
    }

    // COMMAND EXECUTOR
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Check if sender is player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Check arguments
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$label <player>"); return true }

        val targetName = args[0]
        val onlinePlayer = Bukkit.getPlayer(targetName)

        // Player is online - teleport to them
        if (onlinePlayer != null) {
            TeleportBackCommand.updateLastLocation(sender, sender.location)  // Update last location
            sender.teleportAsync(onlinePlayer.location)                                  // Teleport
            sender.sendMessage("§7\uD83C\uDF00 Teleported to $targetName")               // Send feedback
            return true
        }

        // Player is offline - check for logout location
        val offlineLocation = LOGOUT_PLAYER_LOCATIONS[targetName]
        if (offlineLocation == null) {
            sender.sendMessage("§cNo logout location found for $targetName")
            return true
        }

        // Teleport to offline location
        TeleportBackCommand.updateLastLocation(sender, sender.location)            // Update last location
        sender.teleportAsync(offlineLocation)                                                  // Teleport
        sender.sendMessage("§7\uD83C\uDF00 Teleported to $targetName's last logout location")  // Send feedback
        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return LOGOUT_PLAYER_LOCATIONS.keys.filter {
                it.lowercase().startsWith(args[0].lowercase()) && Bukkit.getPlayer(it) == null
            }
        }
        return emptyList()
    }

}
