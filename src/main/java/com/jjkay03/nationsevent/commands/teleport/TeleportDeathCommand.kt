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
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TeleportDeathCommand(private val commandName: String) : CommandExecutor, TabCompleter, Listener {

    companion object {
        private val DEATH_LOCATIONS = ConcurrentHashMap<UUID, Location>()

        // Update death location for a player
        fun updateDeathLocation(player: Player, location: Location) {
            DEATH_LOCATIONS[player.uniqueId] = location.clone()
        }

        // Get death location for a player
        fun getDeathLocation(playerUUID: UUID): Location? {
            return DEATH_LOCATIONS[playerUUID]
        }
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
        val targetPlayerName: String = if (args.isNotEmpty()) {
            args[0]
        } else {
            (sender as Player).name
        }

        // Find player (online or offline)
        val targetPlayer = Bukkit.getPlayerExact(targetPlayerName) ?: Bukkit.getOfflinePlayer(targetPlayerName)

        // Check if player exists
        if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline) {
            sender.sendMessage("§cPlayer '$targetPlayerName' not found!")
            return true
        }

        // Get death location
        val deathLocation = DEATH_LOCATIONS[targetPlayer.uniqueId]
        if (deathLocation == null) {
            val message =
                if (args.isEmpty() && sender is Player) "§cYou don't have a death location saved!"
                else "§c${targetPlayer.name} doesn't have a death location saved!"
            sender.sendMessage(message)
            return true
        }

        // Get executor (for teleporting)
        val executor: Player = if (sender is Player) {
            sender
        } else {
            sender.sendMessage("§cConsole cannot teleport!")
            return true
        }

        // Update last location before teleport
        TeleportBackCommand.updateLastLocation(executor, executor.location)

        // Teleport player
        executor.teleportAsync(deathLocation)

        // Send feedback
        val successMessage =
            if (args.isEmpty()) "§7🌀 Teleported to your death location"
            else "§7🌀 Teleported to ${targetPlayer.name}'s death location"
        sender.sendMessage(successMessage)

        return true
    }

    // TAB COMPLETION - Only show players who have death locations saved
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            // Get all players (online and offline) who have death locations
            val playersWithDeaths = DEATH_LOCATIONS.keys.mapNotNull { uuid ->
                Bukkit.getOfflinePlayer(uuid).name
            }

            return playersWithDeaths
                .filter { it.startsWith(args[0], ignoreCase = true) }
                .sorted()
        }
        return emptyList()
    }

    // EVENT LISTENER - Save death location when player dies
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        updateDeathLocation(player, player.location)
    }

}