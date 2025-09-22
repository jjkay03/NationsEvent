package com.jjkay03.nationsevent.commands.player_scale

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class PlayerScaleCommand(private val commandName: String) : CommandExecutor {

    // REGISTER COMMAND
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        // Ensure at least 1 argument is provided (player name)
        if (args.isEmpty()) { sender.sendMessage("§cUsage: /$label <player> [scale]"); return true }

        // Get the player from the first argument
        val targetPlayer = Bukkit.getPlayer(args[0])
        if (targetPlayer == null) { sender.sendMessage("§cPlayer not found!"); return true }

        // Parse the scale value from the second argument, or default to 1.0 if not provided
        val scale: Float = if (args.size == 2) {
            try {
                args[1].toFloat()
            } catch (e: NumberFormatException) {
                sender.sendMessage("§cInvalid scale! Must be a number.")
                return true
            }
        } else {
            1.0f  // Default scale if no scale argument is provided
        }

        // Validate the scale (must be positive and non-zero)
        if (scale <= 0) { sender.sendMessage("§cScale must be greater than 0"); return true }

        // Set the player's scale using the generic scale attribute (scheduler thread safe)
        Scheduler.task(Scheduler.SchedulerType.PLAYER, {
            val scaleAttribute = targetPlayer.getAttribute(Attribute.SCALE)
            if (scaleAttribute != null) {
                scaleAttribute.baseValue = scale.toDouble()
                sender.sendMessage("§6Set scale of ${targetPlayer.name} to $scale")
            } else {
                sender.sendMessage("§cFailed to set scale for ${targetPlayer.name}!")
            }
        }, player = targetPlayer)

        return true
    }
}