package com.jjkay03.nationsevent.commands.others

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FlySpeedCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // End if not player
        if (sender !is Player) { sender.sendMessage("§cOnly players can use this command!"); return true }

        // No args = reset to default
        if (args.isEmpty()) {
            sender.flySpeed = 0.1f
            sender.sendMessage("§6Fly speed reset to default")
            return true
        }

        // Parse speed value
        val speed = args[0].toFloatOrNull()
        if (speed == null || speed < 0f || speed > 10f) {
            sender.sendMessage("§cUsage: /$commandName <0-10>")
            return true
        }

        // Bukkit fly speed range is -1 to 1, so divide by 10
        sender.flySpeed = (speed / 10f).coerceIn(-1f, 1f)
        sender.sendMessage("§6Fly speed set to $speed")

        return true
    }
}
