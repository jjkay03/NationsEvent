package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FlyCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // Toggle fly mode
        sender.allowFlight = !sender.allowFlight
        val status = if (sender.allowFlight) "enabled" else "disabled"
        sender.sendMessage("§6Flight mode $status")

        return true
    }
}
