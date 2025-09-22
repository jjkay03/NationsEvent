package com.jjkay03.nationsevent.commands.announce

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.utils.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinStageCommand(private val commandName: String) : CommandExecutor {

    // REGISTER COMMAND
    init {
        NationsEvent.INSTANCE.getCommand(commandName)?.setExecutor(this)
    }

    // COMMAND
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // End command if sender is not a player
        if (sender !is Player) { sender.sendMessage("§cOnly players can run this command!"); return true }

        // Send a title to all online players
        Bukkit.getOnlinePlayers().forEach { player ->
            Scheduler.task(Scheduler.SchedulerType.PLAYER, {
                player.sendTitle("§a\uD83D\uDD0A", "§aJoin stage channel!", 10, 100, 10)
                player.sendMessage("§a\uD83D\uDD0A Join stage channel!")
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            }, player = player)
        }

        return true
    }
}