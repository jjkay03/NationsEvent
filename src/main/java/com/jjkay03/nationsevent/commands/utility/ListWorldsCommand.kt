package com.jjkay03.nationsevent.commands.utility

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ListWorldsCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Get all worlds
        val worlds = Bukkit.getWorlds()

        // Header
        sender.sendMessage("§r")
        sender.sendMessage("§6Loaded worlds:")

        // List all worlds with player count
        worlds.forEach { world ->
            val playerCount = world.players.size
            sender.sendMessage("§f- ${world.name} §7(Players: §a$playerCount§7)")
        }

        sender.sendMessage("§r")

        return true
    }

}
