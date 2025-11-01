package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GodCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Toggle god mode
        sender.isInvulnerable = !sender.isInvulnerable
        val status = if (sender.isInvulnerable) "enabled" else "disabled"
        sender.sendMessage("§6God mode $status")

        return true
    }
}
