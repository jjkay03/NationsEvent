package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SessionTimeCommand: CommandExecutor {
    // Command
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // End if session did start yetS
        if (!NationsEvent.SESSION_STARTED) { sender.sendMessage("§cSession did not start yet!"); return true }

        // Send player message
        sender.sendMessage("§a⌚ Time since session start: ${getTimeElapsed()}")

        return true
    }

    // Get elapsed time since session start
    private fun getTimeElapsed(): String {
        val currentTime = System.currentTimeMillis()
        val elapsedTimeMillis = currentTime - NationsEvent.SESSION_START_TIME

        // Convert milliseconds to hours and minutes
        val hours = (elapsedTimeMillis / (1000 * 60 * 60)) % 24
        val minutes = (elapsedTimeMillis / (1000 * 60)) % 60

        return String.format("%02d:%02d", hours, minutes)
    }
}