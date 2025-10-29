package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TeleportTopCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if console
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Update player last location before teleport
        TeleportBackCommand.updateLastLocation(sender, sender.location)

        // Get current location
        val currentLocation = sender.location
        val world = currentLocation.world
        val blockX = currentLocation.blockX
        val blockZ = currentLocation.blockZ

        // Get the highest block at current X, Z coordinates
        val y = world.getHighestBlockYAt(blockX, blockZ).toDouble() + 1.0
        val topLocation = Location(world, currentLocation.x, y, currentLocation.z, currentLocation.yaw, currentLocation.pitch)

        sender.teleportAsync(topLocation)

        // Send feedback
        //sender.sendMessage("§7🌀 Teleported to top")

        return true
    }

}
