package com.jjkay03.nationsevent.commands.player_scale

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class PlayerScaleRestAllCommand(private val commandName: String) : CommandExecutor {

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // Check if the correct confirmation is provided
        if (args.isEmpty() || args[0] != "CONFIRM") {
            sender.sendMessage("§cTo reset all players scales, type: /$label CONFIRM")
            return true
        }

        val onlinePlayers = Bukkit.getOnlinePlayers()
        var processedCount = 0
        val totalCount = onlinePlayers.size

        // Iterate through all online players (scheduler thread safe)
        for (player in onlinePlayers) {
            Scheduler.task(Scheduler.SchedulerType.PLAYER, {
                // Get the player's scale attribute
                val scaleAttribute = player.getAttribute(Attribute.SCALE)

                // Reset the player's scale to 1 if the attribute is available
                if (scaleAttribute != null) scaleAttribute.baseValue = 1.0

                processedCount++

                // Send completion message when all players are processed
                if (processedCount == totalCount) sender.sendMessage("§6All players scales have been reset to default (1)")

            }, player = player)
        }

        // If no players online, send immediate message
        if (totalCount == 0) sender.sendMessage("§6No online players to reset scales for")

        return true
    }
}
