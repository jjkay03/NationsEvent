package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator

class TeleportJumpCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Get destination
        val destination = getJumpLocation(sender)

        // End if destinations is null
        if (destination == null) { sender.sendMessage("§cNo valid location found to jump to!"); return true }

        // Update player last location before teleport
        TeleportBackCommand.updateLastLocation(sender, sender.location)

        // Teleport player
        sender.teleportAsync(destination)
        return true
    }

    // Helper function to get jump location
    private fun getJumpLocation(player: Player): org.bukkit.Location? {
        val world = player.world
        val eyeLocation = player.eyeLocation
        val iterator = BlockIterator(world, eyeLocation.toVector(), eyeLocation.direction, 0.0, 100)
        var lastAirBlock: Block? = null

        // Go through blocks to find a valid block
        while (iterator.hasNext()) {
            val block = iterator.next()

            // Found a solid block — return location one block above it
            if (block.type.isSolid) {
                return block.location.add(0.5, 1.0, 0.5).apply {
                    yaw = player.location.yaw
                    pitch = player.location.pitch
                }
            }

            lastAirBlock = block
        }

        // Return last air block if no solid block was found
        return lastAirBlock?.location?.add(0.5, 0.0, 0.5)?.apply {
            yaw = player.location.yaw
            pitch = player.location.pitch
        }
    }
}
