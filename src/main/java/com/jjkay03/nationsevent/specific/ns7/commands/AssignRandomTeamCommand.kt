package com.jjkay03.nationsevent.specific.ns7.commands

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.specific.ns7.AssignRandomTeam
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class AssignRandomTeamCommand(private val commandName: String) : CommandExecutor, TabCompleter {

    // INITIALIZATION
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
        NationsEvent.INSTANCE.getCommand(commandName)?.tabCompleter = this
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        val invalidArguments = "§cInvalid argument, usage: /$label on/off"
        if (args.isEmpty()) { sender.sendMessage(invalidArguments); return true }
        when (args[0].lowercase()) {
            "on" -> { AssignRandomTeam.ENABLED = true; sender.sendMessage("§7🎲 Assign Random Team has been §aENABLED") }
            "off" -> { AssignRandomTeam.ENABLED = false; sender.sendMessage("§7🎲 Assign Random Team has been §cDISABLED") }
            else -> sender.sendMessage(invalidArguments)
        }
        return true
    }

    // TAB COMPLETER
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String>? {
        if (args.size == 1) {
            val completions = mutableListOf("on", "off")
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return null
    }

}
