package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TeleportPlayerCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if invalid args
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$label <player> [player2]"); return true }

        // Get first player (target or destination depending on args)
        val player1 = Bukkit.getPlayer(args[0]) ?: run {
            sender.sendMessage("§cPlayer '${args[0]}' not found!")
            return true
        }

        // Two arguments: teleport player1 to player2
        if (args.size >= 2) {
            val player2 = Bukkit.getPlayer(args[1]) ?: run { sender.sendMessage("§cPlayer '${args[1]}' not found!"); return true }

            // Can't teleport player to themselves
            if (player1 == player2) { sender.sendMessage("§cYou can't teleport a player to themselves!"); return true }

            // Update player last location before teleport
            TeleportBackCommand.updateLastLocation(player1, player1.location)

            // Teleport player1 to player2
            player1.teleportAsync(player2.location)

            // Send feedback
            sender.sendMessage("§7\uD83C\uDF00 Teleported ${player1.name} to ${player2.name}")
        }

        // One argument: teleport sender ro player1
        else {
            // End if console
            if (sender !is Player) { sender.sendMessage("§cConsole must specify two players!"); return true }

            // Can't teleport to yourself
            if (player1 == sender) { sender.sendMessage("§cYou can't teleport to yourself!"); return true }

            // Update player last location before teleport
            TeleportBackCommand.updateLastLocation(sender, sender.location)

            // Teleport sender to player1
            sender.teleportAsync(player1.location)

            // Send feedback
            sender.sendMessage("§7\uD83C\uDF00 Teleported to ${player1.name}")
        }

        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        if (args.size == 2) {
            return Bukkit.getOnlinePlayers()
                .filter { it.name != args[0] } // Don't suggest same player
                .map { it.name }
                .filter { it.startsWith(args[1], ignoreCase = true) }
        }
        return emptyList()
    }

}
