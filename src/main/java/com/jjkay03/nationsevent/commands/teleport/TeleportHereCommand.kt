package com.jjkay03.nationsevent.commands.teleport

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TeleportHereCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND EXECUTION
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // End if console
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Must specify a player
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$commandName <player>"); return true }

        // Get target player
        val targetPlayer = Bukkit.getPlayer(args[0]) ?: run {
            sender.sendMessage("§cPlayer '${args[0]}' not found!")
            return true
        }

        // Can't teleport yourself to yourself
        if (targetPlayer == sender) { sender.sendMessage("§cYou can't teleport yourself to yourself!"); return true }

        // Teleport target to sender
        targetPlayer.teleport(sender.location)

        // Send feedback
        sender.sendMessage("§aTeleported ${targetPlayer.name} to your location")
        targetPlayer.sendMessage("§eTeleported to ${sender.name}")

        return true
    }

    // TAB COMPLETION
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers()
                .filter { it != sender } // Don't include sender in suggestions
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
