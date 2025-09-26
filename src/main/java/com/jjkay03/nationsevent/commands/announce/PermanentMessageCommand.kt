package com.jjkay03.nationsevent.commands.announce

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import net.kyori.adventure.text.minimessage.MiniMessage

class PermanentMessageCommand(private val commandName: String) : CommandExecutor {

    companion object {
        var MESSAGE: String? = null
        var MESSAGE_TASK: Any? = null
    }

    // INITIALIZATION (Register command)
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            // Stop the message task if running
            if (MESSAGE_TASK != null) {
                sender.sendMessage("§aPermanent message cleared")
                Scheduler.cancelTask(MESSAGE_TASK)
                MESSAGE_TASK = null
                MESSAGE = null
            }
            // Error if no task already running
            else {
                sender.sendMessage("§cNo permanent message is currently running!\nTo set one, use: /$label <message>")
            }
            return true
        }

        // Format message
        MESSAGE = "<red>" + args.joinToString(" ")
        val messageComponent = MiniMessage.miniMessage().deserialize(MESSAGE!!)

        // Send feedback
        val feedbackPrefix = MiniMessage.miniMessage().deserialize("<green>Permanent message set to: ")
        sender.sendMessage(feedbackPrefix.append(messageComponent))

        // If a task is already running, update the message only
        if (MESSAGE_TASK != null) return true

        // Start a new task to broadcast the message
        MESSAGE_TASK = Scheduler.taskRepeating(
            type = Scheduler.SchedulerType.GLOBAL,
            delayTicks = 1L,   // Initial delay
            periodTicks = 40L, // Period (2 seconds)
            task = {
                // Send the message to all players
                if (MESSAGE != null) {
                    val currentMessageComponent = MiniMessage.miniMessage().deserialize(MESSAGE!!)
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.sendActionBar(currentMessageComponent)
                    }
                }
            }
        )

        return true
    }
}
