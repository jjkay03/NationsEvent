package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TeleportWorldCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if no arguments
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$commandName <world> [player]"); return true }

        // Get target world
        val worldName = args[0]
        val targetWorld = Bukkit.getWorld(worldName)
        if (targetWorld == null) { sender.sendMessage("§cWorld '$worldName' not found!"); return true }

        // Two arguments: teleport specified player to world
        if (args.size >= 2) {
            val targetPlayer = Bukkit.getPlayer(args[1])
            if (targetPlayer == null) { sender.sendMessage("§cPlayer '${args[1]}' not found!"); return true }

            // Update player last location before teleport
            TeleportBackCommand.updateLastLocation(targetPlayer, targetPlayer.location)

            // Get safe spawn location and teleport
            getSafeSpawnLocation(targetWorld) { spawnLocation ->
                targetPlayer.teleportAsync(spawnLocation)

                // Send feedback
                sender.sendMessage("§7🌀 Teleported ${targetPlayer.name} to world '${targetWorld.name}'")
                if (sender != targetPlayer) targetPlayer.sendMessage("§7🌀 Teleported to world '${targetWorld.name}'")
            }
        }

        // One argument: teleport sender to world
        else {
            // End if console
            if (sender !is Player) { sender.sendMessage("§cConsole must specify a player!"); return true }

            // Update player last location before teleport
            TeleportBackCommand.updateLastLocation(sender, sender.location)

            // Get safe spawn location and teleport
            getSafeSpawnLocation(targetWorld) { spawnLocation ->
                sender.teleportAsync(spawnLocation)

                // Send feedback
                sender.sendMessage("§7🌀 Teleported to world '${targetWorld.name}'")
            }
        }

        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // First argument: world names
        if (args.size == 1) {
            return Bukkit.getWorlds()
                .map { it.name }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
                .toMutableList()
        }

        // Second argument: player names
        if (args.size == 2) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.lowercase().startsWith(args[1].lowercase()) }
                .toMutableList()
        }

        return mutableListOf()
    }

    // Helper function to get safe spawn location at 0, 0
    private fun getSafeSpawnLocation(world: org.bukkit.World, callback: (Location) -> Unit) {
        val spawnLocation = Location(world, 0.5, 64.0, 0.5)

        // Load chunk at 0, 0 and get the highest block
        Scheduler.task(
            type = Scheduler.SchedulerType.REGION,
            location = spawnLocation,
            task = {
                // Get the highest block at 0, 0 that has sky access
                val y = world.getHighestBlockYAt(0, 0).toDouble() + 1.0
                val finalLocation = Location(world, 0.5, y, 0.5)

                callback(finalLocation)
            }
        )
    }

}
