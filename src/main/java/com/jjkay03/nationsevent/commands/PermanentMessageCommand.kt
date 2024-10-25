package com.jjkay03.nationsevent.commands

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable
import net.kyori.adventure.text.Component

class PermanentMessageCommand() : CommandExecutor {

    private var permanentMessage: String? = null
    private var messageTask: BukkitRunnable? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            // Stop the message task if running
            if (messageTask != null) {
                sender.sendMessage("§aPermanent message cleared")
                messageTask!!.cancel()
                messageTask = null
            }
            // Error if no task already running
            else {
                sender.sendMessage("§cNo permanent message is currently running!")
            }
            return true
        }

        // Set the new message
        permanentMessage = args.joinToString(" ")
        sender.sendMessage("§aPermanent message set to: §f$permanentMessage")

        // If a task is already running, update the message only
        if (messageTask != null) { return true }

        // Start a new task to broadcast the message every 2 seconds
        messageTask = object : BukkitRunnable() {
            override fun run() {
                // Send the message to all players
                if (permanentMessage != null) {
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.sendActionBar(Component.text("§c$permanentMessage"))
                    }
                }
            }
        }
        messageTask!!.runTaskTimer(NationsEvent.INSTANCE, 0L, 40L)

        return true
    }
}
