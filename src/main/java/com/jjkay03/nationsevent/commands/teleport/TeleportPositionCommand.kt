package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TeleportPositionCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if invalid args
        if (args.size < 3) { sender.sendMessage("§cUsage: /$label <x> <y> <z> [player]"); return true }

        // Four arguments: teleport specified player to location
        if (args.size >= 4) {
            val targetPlayer = Bukkit.getPlayer(args[3]) ?: run { sender.sendMessage("§cPlayer '${args[3]}' not found!"); return true }

            // Parse coordinates with relative support
            val coords = parseCoordinates(sender, args, targetPlayer.location) ?: return true
            val (x, y, z) = coords

            // Update player last location before teleport
            TeleportBackCommand.updateLastLocation(targetPlayer, targetPlayer.location)

            // Create location in target player's world
            val location = Location(targetPlayer.world, x, y, z)

            // Teleport target player
            targetPlayer.teleportAsync(location)

            // Send feedback
            sender.sendMessage("§7\uD83C\uDF00 Teleported ${targetPlayer.name} to ${x.toInt()}, ${y.toInt()}, ${z.toInt()}")
        }

        // Three arguments: teleport sender to location
        else {
            // End if console
            if (sender !is Player) { sender.sendMessage("§cConsole must specify a player!"); return true }

            // Parse coordinates with relative support
            val coords = parseCoordinates(sender, args, sender.location) ?: return true
            val (x, y, z) = coords

            // Update player last location before teleport
            TeleportBackCommand.updateLastLocation(sender, sender.location)

            // Create location in sender's world
            val location = Location(sender.world, x, y, z)

            // Teleport sender
            sender.teleportAsync(location)

            // Send feedback
            sender.sendMessage("§7\uD83C\uDF00 Teleported to ${x.toInt()}, ${y.toInt()}, ${z.toInt()}")
        }

        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size in 1..3) {
            // Suggest coordinates based on sender's location if they're a player
            if (sender is Player) {
                val coordinate = when (args.size) {
                    1 -> sender.location.blockX.toString()
                    2 -> sender.location.blockY.toString()
                    3 -> sender.location.blockZ.toString()
                    else -> return emptyList()
                }
                return listOf("~", coordinate).filter { it.startsWith(args[args.size - 1], ignoreCase = true) }
            }
        }
        if (args.size == 4) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[3], ignoreCase = true) }
        }
        return emptyList()
    }

    // Helper function to parse all three coordinates
    private fun parseCoordinates(sender: CommandSender, args: Array<out String>, currentLocation: Location): Triple<Double, Double, Double>? {
        val x = parseCoordinate(args[0], currentLocation.x) ?: run { sender.sendMessage("§cInvalid X coordinate!"); return null }
        val y = parseCoordinate(args[1], currentLocation.y) ?: run { sender.sendMessage("§cInvalid Y coordinate!"); return null }
        val z = parseCoordinate(args[2], currentLocation.z) ?: run { sender.sendMessage("§cInvalid Z coordinate!"); return null }
        return Triple(x, y, z)
    }

    // Helper function to parse coordinate with relative support
    private fun parseCoordinate(input: String, current: Double): Double? {
        return when {
            input == "~" -> current
            input.startsWith("~") -> {
                val offset = input.substring(1).toDoubleOrNull() ?: return null
                current + offset
            }
            else -> input.toDoubleOrNull()
        }
    }

}
